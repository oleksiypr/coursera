package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    var b, i = 0
    while (b >= 0 && i < chars.length) {
      if (chars(i) == '(') b+= 1
      if (chars(i) == ')') b-= 1
      i += 1
    }
    b == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    /**
      * @param notBalancedOpen count of non balanced '('
      * @param notBalancedClose count of non balanced ')'
      * @return tuple of non balanced '(' and ')'
      */
    @tailrec
    def traverse(from: Int, until: Int, notBalancedOpen: Int, notBalancedClose: Int) : (Int, Int) = {
      if (from >= until) (notBalancedOpen, notBalancedClose) else
      if (chars(from) == '(') traverse(from + 1, until, notBalancedOpen + 1, notBalancedClose) else
      if (chars(from) == ')') {
        if (notBalancedOpen > 0) traverse(from + 1, until, notBalancedOpen - 1, notBalancedClose)
        else traverse(from + 1, until, notBalancedOpen, notBalancedClose + 1)
      } else traverse(from + 1, until, notBalancedOpen, notBalancedClose)
    }

    def reduce(from: Int, until: Int) : (Int, Int) = {
      if (until - from <= threshold) traverse(from, until, 0, 0)
      else {
        val m = from + (until - from)/2
        val ((a1, b1), (a2, b2)) = parallel(
          reduce(from, m),
          reduce(m, until)
        )

        val x = a2 + (if (a1 >= b2) a1 - b2 else 0)
        val y = b1 + (if (b2 >= a1) b1 - a2 else 0)
        (x, y)
      }
    }

    reduce(0, chars.length) == (0, 0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
