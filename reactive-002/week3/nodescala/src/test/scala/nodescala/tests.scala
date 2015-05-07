package nodescala

import scala.language.postfixOps
import scala.util.{Try, Success, Failure}
import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.async.Async.{async, await}
import org.scalatest._
import NodeScala._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NodeScalaSuite extends FunSuite {
  test("A Future should always be completed") {
    val always = Future.always(517)
    assert(Await.result(always, 0 nanos) == 517)
  }
  
  test("A Future should never be completed") {
    val never = Future.never[Int]
    try {
      Await.result(never, 1 second)
      assert(false)
    } catch {
      case t: TimeoutException => // ok!
    }
  }  

  test("A Future should be completed aftre time `t`") {
    val delayed = Future.delay(2 second)
    try {
      Await.result(delayed, 1 second)
      assert(false)
    } catch {
      case t: TimeoutException => // ok!
    }
    Await.ready(delayed, 2 second)
  }

  test("Any Future holding the value of the futures list that completed first") {
    val f1 = Future { Thread.sleep(200); "f1" }
    val f2 = Future { Thread.sleep( 50); "f2" }
    val success  = Future.any(List(f1, f2))    
    assert(Await.result(success, 55 millis) == "f2")    

    val f3 = Future { Thread.sleep(20); "f3" }
    val f4 = Future { Thread.sleep( 5); throw new Exception }
    val fail = Future.any(List(f3, f4))
    try {
      val x = Await.result(fail, 10 millis)
      assert(false)
    } catch {
      case t: TimeoutException => throw t
      case _: Exception => assert(true)
    }
  }
  
  test("All futures turns to future of all") {
    val fs = List(Future.always(1), Future.always(2), Future.always(3))
    assert(Await.result(Future.all(fs), 5 milli) === List(1, 2, 3))
    
    val fail = Future.all(List(Future.always(1), Future(throw new Exception)))
    try {
      Await.result(fail, 5 millis)
      assert(false)
    } catch {
      case t: TimeoutException => throw t
      case _: Exception => assert(true)
    }
  }

  test("Now") {
    val always = Future.always("now")
    assert(always.now == "now")

    val never = Future.never
    try {
      never.now
      assert(false)
    } catch {
      case ex: NoSuchElementException => assert(true)
    }

    val f = Future { Thread.sleep(100); "f" }
    Thread.sleep(50)
    try {
      f.now
      assert(false)
    } catch {
      case ex: NoSuchElementException => assert(true)
    }
    Thread.sleep(50)
    assert(f.now == "f")
  }

  test("A Future continue with") {
    val ok = Future { 1 }
    def cont(f: Future[Int]): Int = 2
    assert(Await.result(ok.continueWith(cont), 10 milli) == 2)

    val fail = Future.failed[Int](new RuntimeException)
    assert(Await.result(fail.continueWith(cont), 10 milli) == 2)
  }

  test("A Future continue") {
    val f = Future { 1 }
    def cont(f: Try[Int]): Int = 2
    assert(Await.result(f.continue(cont), 10 milli) == 2)
  }

  test("Run future") {
    val working = Future.run() { ct =>
      Future {
        var n = 0
        while (ct.nonCancelled) {
          n += 1
        }
      }
    }
    Future.delay(2 seconds) onSuccess {
      case _ => working.unsubscribe()
    }
  }
  
  class DummyExchange(val request: Request) extends Exchange {
    @volatile var response = ""
    val loaded = Promise[String]()
    def write(s: String) {
      response += s
    }
    def close() {
      loaded.success(response)
    }
  }

  class DummyListener(val port: Int, val relativePath: String) extends NodeScala.Listener {
    self =>

    @volatile private var started = false
    var handler: Exchange => Unit = null

    def createContext(h: Exchange => Unit) = this.synchronized {
      assert(started, "is server started?")
      handler = h
    }

    def removeContext() = this.synchronized {
      assert(started, "is server started?")
      handler = null
    }

    def start() = self.synchronized {
      started = true
      new Subscription {
        def unsubscribe() = self.synchronized {
          started = false
        }
      }
    }

    def emit(req: Request) = {
      val exchange = new DummyExchange(req)
      if (handler != null) handler(exchange)
      exchange
    }
  }

  class DummyServer(val port: Int) extends NodeScala {
    self =>
    val listeners = mutable.Map[String, DummyListener]()

    def createListener(relativePath: String) = {
      val l = new DummyListener(port, relativePath)
      listeners(relativePath) = l
      l
    }

    def emit(relativePath: String, req: Request) = this.synchronized {
      val l = listeners(relativePath)
      l.emit(req)
    }
  }

  test("Server should serve requests") {
    val dummy = new DummyServer(8191)
    val dummySubscription = dummy.start("/testDir") {
      request => for (kv <- request.iterator) yield (kv + "\n").toString
    }

    // wait until server is really installed
    Thread.sleep(500)

    def test(req: Request) {
      val webpage = dummy.emit("/testDir", req)
      val content = Await.result(webpage.loaded.future, 1 second)
      val expected = (for (kv <- req.iterator) yield (kv + "\n").toString).mkString
      assert(content == expected, s"'$content' vs. '$expected'")
    }

    test(immutable.Map("StrangeRequest" -> List("Does it work?")))
    test(immutable.Map("StrangeRequest" -> List("It works!")))
    test(immutable.Map("WorksForThree" -> List("Always works. Trust me.")))

    dummySubscription.unsubscribe()
  }

  test("Server should be stoppable if receives infinite  response") {
    val dummy = new DummyServer(8191)
    val dummySubscription = dummy.start("/testDir") {
      request => Iterator.continually("a")
    }

    // wait until server is really installed
    Thread.sleep(500)

    val webpage = dummy.emit("/testDir", Map("Any" -> List("thing")))
    try {
      // let's wait some time
      Await.result(webpage.loaded.future, 1 second)
      fail("infinite response ended")
    } catch {
      case e: TimeoutException =>
    }

    // stop everything
    dummySubscription.unsubscribe()
    Thread.sleep(500)
    webpage.loaded.future.now // should not get NoSuchElementException
  }

  test("Server should be able to handle two concurrent requests") {
    val dummy = new DummyServer(8191)
    val dummySubscription = dummy.start("/testDir") {
      request =>
        {
          Thread.sleep(800)
          Iterator.empty
        }
    }

    // wait until server is really installed
    Thread.sleep(500)

    val webpage1 = dummy.emit("/testDir", Map("Any" -> List("thing")))
    Thread.sleep(100) // allow new context to be created
    val webpage2 = dummy.emit("/testDir", Map("Any" -> List("thing")))

    // let's wait some time; this should load both pages
    Await.result(webpage2.loaded.future, 1 second)

    webpage2.loaded.future.now // should not get NoSuchElementException
    dummySubscription.unsubscribe()
  }
}


