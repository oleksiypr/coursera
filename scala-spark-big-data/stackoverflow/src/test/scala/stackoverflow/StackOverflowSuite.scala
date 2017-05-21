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

  test("scoredPostings") {
    val question = Posting(
      postingType = Question,
      id = 0,
      acceptedAnswer = None,
      parentId = None,
      score = 2,
      tags = None
    )
    val answer1 = Posting(
      postingType = Answer,
      id = 1,
      acceptedAnswer = None,
      parentId = Some(0),
      score = 3,
      tags = None
    )
    val answer2 = Posting(
      postingType = Answer,
      id = 2,
      acceptedAnswer = None,
      parentId = Some(0),
      score = 4,
      tags = None
    )

    val rdd: RDD[(Int, Iterable[(Posting, Posting)])] =
      sc.parallelize(List(
        (question.id, List(
          (question, answer1),
          (question, answer2)
        )))
      )

    import testObject.scoredPostings
    val res = scoredPostings(rdd).collect()
    assert(res.length == 1)
    assert(res(0) == (question, 4))
  }
}
