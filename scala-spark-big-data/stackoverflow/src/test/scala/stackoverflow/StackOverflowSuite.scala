package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {
  import StackOverflow.{Question, Answer}

  lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("StackOverflowSuite")
  lazy val sc: SparkContext = new SparkContext(conf)

  override def afterAll(): Unit = sc.stop()

  lazy val testObject = new StackOverflow {
    override val langs = List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  test("groupedPostings returns empty RDD") {
    import testObject.groupedPostings

    val emptyPosts: RDD[Posting] = sc.parallelize(Nil)
    assert(groupedPostings(emptyPosts).count() == 0)

    val singleAnswer = sc.parallelize(
      List(
        Posting(
          postingType = Answer,
          id = 1,
          acceptedAnswer = None,
          parentId = None,
          score = 3,
          tags = None
        )
      )
    )
    assert(groupedPostings(singleAnswer).count() == 0)
  }

  test("groupedPostings with a single question") {
    import testObject.groupedPostings
    val question = Posting(
      postingType = Question,
      id = 1,
      acceptedAnswer = None,
      parentId = None,
      score = 2,
      tags = None
    )
    val singleQuestion1 = sc.parallelize(List(question))
    assert(groupedPostings(singleQuestion1).count() == 0)

    val answer = Posting(
      postingType = Answer,
      id = 2,
      acceptedAnswer = None,
      parentId = Some(1),
      score = 2,
      tags = None
    )

    val singleQuestion2 = sc.parallelize(List(question, answer))
    val res2 = groupedPostings(singleQuestion2).collect()
    assert(res2.length == 1)
    assert(res2(0)._2.head == (question, answer))
  }


  /**
    test("'rankLangsUsingIndex' should work for a simple RDD with three elements") {
      assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
      import WikipediaRanking._
      val langs = List("Scala", "Java")
      val articles = List(
          WikipediaArticle("1","Groovy is pretty interesting, and so is Erlang"),
          WikipediaArticle("2","Scala and Java run on the JVM"),
          WikipediaArticle("3","Scala is not purely functional")
        )
      val rdd = sc.parallelize(articles)
      val index = makeIndex(langs, rdd)
      val ranked = rankLangsUsingIndex(index)
      val res = (ranked.head._1 == "Scala")
      assert(res)
    }

  */
}
