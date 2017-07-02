package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math._

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  val minDist: Double = (1000.0/1852.0/60.0).toRadians
  val p = 3.0d

  def dist(
      location1: Location,
      location2: Location
    ): Double = {

    val w1 = location1.lat.toRadians
    val w2 = location2.lat.toRadians
    val dl = (location1.lon - location2.lon).toRadians

    acos(sin(w1)*sin(w2) + cos(w1)*cos(w2)*cos(dl))
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(
      temperatures: Iterable[(Location, Double)],
      location: Location
    ): Double = {

    def weight(x: Location) = 1.0 / pow(dist(location, x), p)
    def closeEnough(x: (Location, Double)) = dist(location, x._1) < minDist

    def weighted = {
      val ws = temperatures.map(t => weight(t._1))
      val tw = (temperatures.map(_._2) zip ws)
        .map { case (t, w) => t * w }

      tw.sum / ws.sum
    }

    temperatures find closeEnough match {
      case Some(lt) => lt._2
      case None => weighted
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    ???
  }

}

