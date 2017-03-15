package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = oneOf(
    const(empty),
    for {
      x <- arbitrary[Int]
      h <- oneOf(const(empty), genHeap)
    } yield insert(x, h)
  )
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

  property("sorted sequense") = forAll {
    h: H => {
      val list = toList(h)
      list == list.sorted
    }
  }

  private def toList(h: H): List[Int] =
    if (isEmpty(h)) Nil
    else findMin(h) :: toList(deleteMin(h))
}
