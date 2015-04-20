package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {
  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("min of 2 elements") = forAll {(a: Int, b: Int) =>
    import scala.math.min
    val h = insert(a, insert(b, empty))
    val m = min(a, b)
    findMin(h) == m    
  }
  
  property("insert to empty and delete minimum") = forAll { a: Int =>
    val h = insert(a, empty)
    val e = deleteMin(h)
    e == empty
  }
  
  property("sorted sequenc when find and delete minimum") = forAll { h: H =>
    def seqFindMin(h: H): List[Int] = 
      if (isEmpty(h)) Nil else {
        val m = findMin(h)
        val rest = deleteMin(h)
        m :: seqFindMin(rest)
      }
    
    val xs = seqFindMin(h)
    xs == xs.sorted
  }

  property("min of 2 melded heaps") = forAll { (h: H, g: H) =>
      val minH = findMin(h)
      val minG = findMin(g)
      val minMelt = findMin(meld(h, g))
      minMelt == minH || minMelt == minG
  }

  property("meldMinMove") = forAll { (h: H, g: H) =>
    def seqFindMin(h: H): List[Int] =
      if (isEmpty(h)) Nil else {
        val m = findMin(h)
        val rest = deleteMin(h)
        m :: seqFindMin(rest)
      }    
    
    val minH = findMin(h)
    val xs1 = seqFindMin(meld(deleteMin(h), insert(minH, g)))
    val xs2 = seqFindMin(meld(h, g))
    xs1 == xs2
  }
  
  lazy val genHeap: Gen[H] = for {
    x <- arbitrary[Int]   
    h <- oneOf(const(empty), genHeap)
  } yield insert(x, h)
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)
}
