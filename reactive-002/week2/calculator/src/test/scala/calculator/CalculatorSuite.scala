package calculator

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest._

import TweetLength.MaxTweetLength

@RunWith(classOf[JUnitRunner])
class CalculatorSuite extends FunSuite with ShouldMatchers {

  /******************
   ** TWEET LENGTH **
   ******************/

  def tweetLength(text: String): Int =
    text.codePointCount(0, text.length)

  test("tweetRemainingCharsCount with a constant signal") {
    val result = TweetLength.tweetRemainingCharsCount(Var("hello world"))
    assert(result() == MaxTweetLength - tweetLength("hello world"))

    val tooLong = "foo" * 200
    val result2 = TweetLength.tweetRemainingCharsCount(Var(tooLong))
    assert(result2() == MaxTweetLength - tweetLength(tooLong))
  }

  test("tweetRemainingCharsCount with a supplementary char") {
    val result = TweetLength.tweetRemainingCharsCount(Var("foo blabla \uD83D\uDCA9 bar"))
    assert(result() == MaxTweetLength - tweetLength("foo blabla \uD83D\uDCA9 bar"))
  }

  test("colorForRemainingCharsCount with a constant signal") {
    val resultGreen1 = TweetLength.colorForRemainingCharsCount(Var(52))
    assert(resultGreen1() == "green")
    val resultGreen2 = TweetLength.colorForRemainingCharsCount(Var(15))
    assert(resultGreen2() == "green")

    val resultOrange1 = TweetLength.colorForRemainingCharsCount(Var(12))
    assert(resultOrange1() == "orange")
    val resultOrange2 = TweetLength.colorForRemainingCharsCount(Var(0))
    assert(resultOrange2() == "orange")

    val resultRed1 = TweetLength.colorForRemainingCharsCount(Var(-1))
    assert(resultRed1() == "red")
    val resultRed2 = TweetLength.colorForRemainingCharsCount(Var(-5))
    assert(resultRed2() == "red")
  }
  
  /******************
   ** Polynomial **
   ******************/

  test("Polynomial.computeDelta") {
    import Polynomial.computeDelta
    val a = 3.5123645
    val b = 3.1341
    val c = 0.54863

    assert(computeDelta(Var(1.45126), Var(b), Var(0.0))() == b*b)
    assert(b*b - 4*a*c == computeDelta(Var(a), Var(b), Var(c))())    
  }
  
  test("Polynomial.computeSolutions. Empty solution") {
    import Polynomial.computeSolutions
    val a = 1.0
    val b = 1.0
    val c = 1.0
    val delta = -1.0 
    assert(computeSolutions(Var(a), Var(b), Var(c), Var(delta))() === Set.empty)
  }

  test("Polynomial.computeSolutions. Single solution") {
    import Polynomial._
    val a =  1.0
    val b = -2.0
    val c =  1.0
    val experted = -b/(2*a)
    
    assert(computeDelta(Var(a), Var(b), Var(c))() ==  Var(0.0)())
    val solution = computeSolutions(Var(a), Var(b), Var(c), Var(0.0))
    assert(solution() === Set(experted))
  }
  
  test("Polynomial.computeSolutions.") {
    import Polynomial._
    import scala.math.sqrt
    
    val a =  2.541166
    val b =  6.159876
    val c =  3.3694459
    val delta = b*b - 4*a*c
    
    val x1 = (-b - sqrt(delta))/(2*a)
    val x2 = (-b + sqrt(delta))/(2*a)
    val expected = Set(x1, x2)
    
    assert(computeDelta(Var(a), Var(b), Var(c))() >  Var(0.0)())
    val solution = computeSolutions(Var(a), Var(b), Var(c), Var(delta))
    assert(solution() === expected)
  }
}
