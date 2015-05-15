package suggestions

import language.postfixOps
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}
import rx.lang.scala._
import org.scalatest._
import gui._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import akka.dispatch.OnComplete


@RunWith(classOf[JUnitRunner])
class WikipediaApiTest extends FunSuite {

  object mockApi extends WikipediaApi {
    def wikipediaSuggestion(term: String) = Future {
      if (term.head.isLetter) {
        for (suffix <- List(" (Computer Scientist)", " (Footballer)")) yield term + suffix
      } else {
        List(term)
      }
    }
    def wikipediaPage(term: String) = Future {
      "Title: " + term
    }
  }

  import mockApi._

  test("WikipediaApi should make the stream valid using sanitized") {
    val notvalid = Observable.just("erik", "erik meijer", "martin")
    val valid = notvalid.sanitized

    var count = 0
    var completed = false
    val sub = valid.subscribe(
      term => {
        assert(term.forall(_ != ' '))
        count += 1
      },
      th => assert(false, s"stream error $th"),
      () => completed = true
    )
    assert(completed && count == 3, "completed: " + completed + ", event count: " + count)
  }

  test("WikipediaApi should correctly use recovered") {
    val ex = new RuntimeException
    val notvalid = Observable.just(1, 2, 3, ex, 4) map {
      case n: Int              => n
      case e: RuntimeException => throw e
    }
    val valid = notvalid.recovered

    var actual = List[Try[Any]]()
    var completed = false
    var noError = true
    val sub = valid.subscribe(
      x => actual = actual :+ x,
      err => noError = false,
      () => completed = true)

    assert(noError, "error arisen")
    assert(completed)
    assert(actual === List(Success(1), Success(2), Success(3), Failure(ex)))
  }
  
  test("WikipediaApi should correctly use timedOut") {
    val events1 = Observable.interval(1 second)
    val to = events1 timedOut 3L
    assert(to.toBlocking.toList === List(0, 1))
    
    val events2 = Observable.interval(1 second) take 1
    val before = events2 timedOut 3L
    assert(before.toBlocking.toList === List(0))
  }
  
  test("WikipediaApi should correctly use concatRecovered") {
    val requests = Observable.just(1, 2, 3)
    val remoteComputation = (n: Int) => Observable.just(0 to n : _*)
    val responses = requests concatRecovered remoteComputation
    val sum = responses.foldLeft(0) { (acc, tn) =>
      tn match {
        case Success(n) => acc + n
        case Failure(t) => throw t
      }
    }
    var total = -1
    val sub = sum.subscribe {
      s => total = s
    }
    assert(total == (1 + 1 + 2 + 1 + 2 + 3), s"Sum: $total")
    
    val repeatedRequests = Observable.just(1, 2, 3).concatRecovered(num => Observable.just(num, num))
    assert(repeatedRequests.toBlocking.toList === 
      List(Success(1), Success(1), Success(2), Success(2), Success(3), Success(3)))
  }
  
  test("WikipediaApi should correctly compose the streams that have errors using concatRecovered") {
    val ex = new RuntimeException
    val requests = Observable.just(1, 2, 3, 4, 5) 
    val remoteComputation = (n: Int) => if (n != 4) Observable.just(n) else Observable.error(ex)
    val result = requests.concatRecovered(remoteComputation) 
    assert(List(Success(1), Success(2), Success(3), Failure(ex), Success(5)) === result.toBlocking.toList)
  }
}
