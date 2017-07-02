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
}
