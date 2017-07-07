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

  test("tile") {
    val colors = List(
      (+32.0, Color(255,   0,   0)),
      (  0.0, Color(  0, 255, 255)),
      (-27.0, Color(255,   0,   0))
    )

    val temperatures = List((Location( 00.000, +020.000), +20.0))
    val img = tile(temperatures, colors, 3, 100, 100)
    val dim = img.dimensions
    assert(dim._2 == 256)

  }
}
