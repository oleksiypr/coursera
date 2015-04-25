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

  test("Polynomial#computeDelta") {
    import Polynomial.computeDelta
    val a = 3.5123645
    val b = 3.1341
    val c = 0.54863

    assert(computeDelta(Var(1.45126), Var(b), Var(0.0))() == b*b)
    assert(delta(a, b, c) == computeDelta(Var(a), Var(b), Var(c))())    
  }
  
  test("Polynomial#computeSolutions. Empty solution") {
    import Polynomial.computeSolutions
    val a = 1.0
    val b = 1.0
    val c = 1.0
    val d = delta(a, b, c)
    assert(d < 0.0)
    assert(computeSolutions(Var(a), Var(b), Var(c), Var(d))() === Set.empty)
  }

  test("Polynomial#computeSolutions. Single solution") {
    import Polynomial._
    val a =  1.0
    val b = -2.0
    val c =  1.0
    val experted = -b/(2*a)
    
    assert(computeDelta(Var(a), Var(b), Var(c))() ==  Var(0.0)())
    val solution = computeSolutions(Var(a), Var(b), Var(c), Var(0.0))
    assert(solution() === Set(experted))
  }
  
  test("Polynomial#computeSolutions.") {
    import Polynomial.computeSolutions
    import scala.math.sqrt
    
    val a =  2.541166
    val b =  6.159876
    val c =  3.3694459
    val d = delta(a, b, c)    
    val expected = solution(a, b, c, d)
    val actual = computeSolutions(Var(a), Var(b), Var(c), Var(d))
    assert(actual() === expected)
  }

  test("Polynomial#computeDelta. Values updated") {
    import Polynomial.computeDelta
    val a0 = 3.5125;  val a1 =  1.25562
    val b0 = 3.1341;  val b1 =  6.16356
    val c0 = 0.5486;  val c1 = -0.6654
    val expected0 = delta(a0, b0, c0)

    val a = Var(a0)
    val b = Var(b0)
    val c = Var(c0)
    val result = computeDelta(a, b, c)
    reset; assert(result() == expected0); a() = a1; assert(result() == delta(a1, b0, c0))
    reset; assert(result() == expected0); b() = b1; assert(result() == delta(a0, b1, c0))
    reset; assert(result() == expected0); c() = c1; assert(result() == delta(a0, b0, c1))
    
    def reset {a() = a0; b() = b0; c() = c0}
  }
  
  test("Polynomial#computeSolutions. Values updated") {
    import Polynomial.computeSolutions
    val a0 =  8.2563;  val a1 = 5.65891
    val b0 =  4.0256;  val b1 = 9.59684
    val c0 = -5.5561;  val c1 = 2.65892
    val d0 = delta(a0, b0, c0); val d1 = delta(a1, b1, c1)
    val expected0 = solution(a0, b0, c0, d0)

    val a = Var(a0)
    val b = Var(b0)
    val c = Var(c0)
    val d = Var(d0)
    val result = computeSolutions(a, b, c, d)
    reset; assert(result() == expected0); a() = a1; assert(result() == solution(a1, b0, c0, d0))
    reset; assert(result() == expected0); b() = b1; assert(result() == solution(a0, b1, c0, d0))
    reset; assert(result() == expected0); c() = c1; assert(result() == solution(a0, b0, c1, d0))
    reset; assert(result() == expected0); d() = d1; assert(result() == solution(a0, b0, c0, d1))
    
    def reset {a() = a0; b() = b0; c() = c0; d() = d0}
  }
  
  /******************
   ** Calculator   **
   ******************/
  
  test("Calculator#eval. Literal") {
    import Calculator.eval
    val literal = 100.500
    assert(eval(Literal(literal), Map()) == literal)
  } 
  
  test("Calculator#eval. Arysmetic") {
    import Calculator.eval
    assert(eval(Literal(3.1415), Map()) == 3.1415)
    assert(eval(Plus(Literal(1.0), Literal(2.0)), Map()) == 3.0)
    assert(eval(Minus(Literal(3.0), Literal(1.0)), Map()) == 2.0)
    assert(eval(Times(Literal(5.0), Literal(2.0)), Map()) == 10.0)
    assert(eval(Divide(Literal(10.0), Literal(2.0)), Map()) == 5.0)    
    assert(eval(
      Divide(
        Minus(
          Plus(
            Times(Literal(2.0), Literal(3.0)),
            Literal(1.0)
          ),
          Literal(3.0)
        ),
        Literal(2.0)
       ), Map()) == 2.0)
    }

  test("Calculator#eval. Cyclic simple:  a = b, b = a") { 
    import Calculator.eval
    val circular1: Map[String, Signal[Expr]] = Map("a" -> Signal(Ref("b")), "b" -> Signal(Ref("a")))
    assert(eval(Ref("a"), circular1).isNaN())
  }
  
  test("Calculator#eval. Cyclic complex 1:  a = b, b = c, c = d, d = b") {  
    import Calculator.eval
    val circular2: Map[String, Signal[Expr]] = Map(
        "a" -> Signal(Ref("b")), 
        "b" -> Signal(Ref("c")), 
        "c" -> Signal(Ref("d")), 
        "d" -> Signal(Ref("b")))
    assert(eval(Ref("a"), circular2).isNaN())    
  }

  test("Calculator#eval. Cyclic complex 2: a = 2b + a") {
    import Calculator.eval
    val references: Map[String, Signal[Expr]] = Map(
      "a" -> Signal(Plus(Times(Literal(2.0), Ref("b")), Literal(1.0))),
      "b" -> Signal(Ref("a")))

    assert(eval(Ref("a"), references).isNaN())
  }

  test("Calculator#eval. Rreferences. Simple.") {
    import Calculator.eval
    val references1: Map[String, Signal[Expr]] = Map("a" -> Signal(Literal(0.0)), "b" -> Signal(Literal(1.0)))
    assert(Literal(eval(Ref("b"), references1)) == references1("b")())
    assert(eval(Ref("c"), references1).isNaN())

    val references2: Map[String, Signal[Expr]] = references1 + ("c" -> Signal(Ref("b")))
    assert(Literal(eval(Ref("c"), references2)) == references2("b")())
  }
  
  test("Calculator#eval. Rreferences. Complex: a = 2b + 1") {
    import Calculator.eval
    val references: Map[String, Signal[Expr]] = Map(
        "a" -> Signal(Plus(Times(Literal(2.0), Ref("b")), Literal(1.0))),  
        "b" -> Signal(Literal(3.0)))
        
     assert(eval(Ref("a"), references) == 7.0)
  }
  
  test("Calculator#computeValues. Simple.") {
    import Calculator._
    val a: Var[Expr] = Var(Literal(0.0))
    val b: Var[Expr] = Var(Literal(0.0))        
    val references: Map[String, Signal[Expr]] = Map("a" ->  a.asInstanceOf[Signal[Expr]], "b" -> b.asInstanceOf[Signal[Expr]])
    val valuesSig = computeValues(references)
    val origin = checkSignals(valuesSig)
    
    reset; assert(checkSignals(valuesSig) === origin); a() = Literal(-1.0); assert(checkSignals(valuesSig) === Map("a" -> -1.0, "b" ->  0.0)) 
    reset; assert(checkSignals(valuesSig) === origin); b() = Literal(-1.0); assert(checkSignals(valuesSig) === Map("a" ->  0.0, "b" -> -1.0))
    
    reset; assert(checkSignals(valuesSig) === origin)
    a() = Ref("b")
    b() = Literal(math.Pi)
    assert(checkSignals(valuesSig) === Map("a" -> math.Pi, "b" ->  math.Pi)) 
    
    b() = Literal(math.E)
    assert(checkSignals(valuesSig) === Map("a" -> math.E, "b" ->  math.E)) 
    
    def reset() = { a() = Literal(0.0); b() = Literal(0.0) }
  }

  test("Calculator#computeValues. Complex. a = 2b + 1") {
    import Calculator._
    val a: Var[Expr] = Var(Literal(0.0))
    val b: Var[Expr] = Var(Literal(0.0))
    val references: Map[String, Signal[Expr]] = Map("a" ->  a.asInstanceOf[Signal[Expr]], "b" -> b.asInstanceOf[Signal[Expr]])
    val valuesSig = computeValues(references)
    val origin = checkSignals(valuesSig)
    
    reset; assert(checkSignals(valuesSig) === origin)
    a() = Plus(Times(Literal(2.0), Ref("b")), Literal(1.0))
    b() = Literal(3.0)
    assert(checkSignals(valuesSig) === Map("a" -> 7.0, "b" -> 3.0))
    
    b() = Literal(4.0)
    assert(checkSignals(valuesSig) === Map("a" -> 9.0, "b" -> 4.0))
    
    def reset() = { a() = Literal(0.0); b() = Literal(0.0) }
  }
      
  private def delta(a: Double, b: Double, c: Double) =  b*b - 4*a*c
  private def solution(a: Double, b: Double, c: Double, d: Double): Set[Double] = {
      import math.sqrt
      val x1 = (-b - sqrt(d)) / (2.0 * a)
      val x2 = (-b + sqrt(d)) / (2.0 * a)
      Set(x1, x2)
  }
  
  def checkSignals[T](signals: Map[String, Signal[T]]) = signals map { case (k, s) => (k -> s()) }

}
