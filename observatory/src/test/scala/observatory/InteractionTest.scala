package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {
  import Interaction._

  test("tileLocation corners") {
    assert(tileLocation(zoom = 8, x =   0, y =   0) == Location(+85.05112877980659, -180.0000))
    assert(tileLocation(zoom = 8, x = 255, y = 255) == Location(-84.92832092949963, +178.59375))
  }
}
