package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] =
    for {
      x <- arbitrary[Int]
      h <- oneOf(const(empty), genHeap)
    } yield insert(x, h)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("min of any two elements") = forAll {
    (a: Int, b: Int) => {
      findMin {
        insert(a,
          insert(b, empty)
        )
      } == math.min(a, b)
    }
  }

  property("insert and delete minimum") = forAll {
    (x: Int) => {
      val h = insert(x, empty)
      isEmpty(deleteMin(h))
    }
  }

  property("sorted sequence") = forAll {
    h: H => {
      val list = toList(h)
      list == list.sorted
    }
  }

  property("minimum of meld") = forAll {
    (h1: H, h2: H) => {
      val melded = meld(h1, h2)
      val m = findMin(melded)
      val m1 = findMin(h1)
      val m2 = findMin(h2)
      m == m1 || m == m2
    }
  }

  property("meld min move") = forAll { (h1: H, h2: H) =>
    val min1 = findMin(h1)
    val xs1 = toList(meld(deleteMin(h1), insert(min1, h2)))
    val xs2 = toList(meld(h1, h2))
    xs1 == xs2
  }

  private def toList(h: H): List[Int] =
    if (isEmpty(h)) Nil
    else findMin(h) :: toList(deleteMin(h))
}
