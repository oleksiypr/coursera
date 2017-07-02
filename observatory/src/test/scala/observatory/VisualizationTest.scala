package observatory


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {
  import Visualization._
  import math._

  test("predictTemperature at known point") {
    val ts = List((Location(0.0, 0.0), 10.0))
    val location = Location(0.0, 0.0)

    assert(predictTemperature(ts, location) == 10.0)
  }

  test("predictTemperature closer to the closest point") {
    val a = (Location(+15.000,  000.000), +15.0)
    val b = (Location( 00.000, +020.000), +20.0)

    val t = predictTemperature(List(a , b), Location(0.0, 0.0))
    val dtA = t - a._2
    val dtB = t - b._2

    assert(abs(dtA) < abs(dtB))
  }

  test("interpolateColor for edge cases") {
    val points = List(
      (+32.0, Color(255,   0,   0)),
      (  0.0, Color(  0, 255, 255)),
      (-27.0, Color(255,   0,   0))
    )
    assert(interpolateColor(points, +35.0) == Color(255,   0,   0))
    assert(interpolateColor(points, +32.0) == Color(255,   0,   0))
    assert(interpolateColor(points,   0.0) == Color(  0, 255, 255))
    assert(interpolateColor(points, -27.0) == Color(255,   0,   0))
    assert(interpolateColor(points, -30.0) == Color(255,   0,   0))
  }

  test("interpolateColor for common case") {
    val points = List(
      (+10.0, Color(255,   0,   0)),
      (  0.0, Color(  0, 255, 255))
    )

    assert(interpolateColor(points, +2.0) == Color(51, 204, 204))
  }
}
