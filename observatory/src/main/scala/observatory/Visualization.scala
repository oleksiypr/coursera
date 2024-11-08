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

    val φ1 = location1.lat.toRadians
    val φ2 = location2.lat.toRadians
    val Δλ = (location1.lon - location2.lon).toRadians

    acos(sin(φ1)*sin(φ2) + cos(φ1)*cos(φ2)*cos(Δλ))
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

    def weight(ds: Double) = 1.0 / pow(ds, p)
    def closeEnough(ds_t: (Double, Double)) = ds_t._1 < minDist

    val distTemperatures = for {
      (station, t) <- temperatures
      ds = dist(location, station)
    } yield (ds, t)

    def weighted = {
      val (sum_wt, sum_w) = distTemperatures
        .foldLeft((0.0, 0.0)) { (wtAcc_wAcc, ds_t) =>
          val (wtAcc, wAcc) = wtAcc_wAcc
          val (ds, t) = ds_t

          val w = weight(ds)
          (wtAcc + w*t, wAcc + w)
        }
      sum_wt / sum_w
    }

    distTemperatures find closeEnough match {
      case Some(lt) => lt._2
      case None => weighted
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(
      points: Iterable[(Double, Color)],
      value: Double
    ): Color = {

    import scala.collection.Searching._
    val sorted = points.toArray.sortBy(_._1)
    val min = sorted(0)
    val max = sorted(sorted.length - 1)

    def interpolate(lo: Int, hi:Int)(f: Color => Int) = {
      val chLo = f(sorted(lo)._2)
      val chHi = f(sorted(hi)._2)

      val tLo = sorted(lo)._1
      val tHi = sorted(hi)._1
      val t   = value

      val ch = (chHi - chLo)*(t - tLo)/(tHi - tLo) + chLo
      ch.round.toInt
    }

    if (value >= max._1) max._2 else
    if (value <  min._1) min._2 else {
      sorted.map(_._1).search(value) match {
        case Found(i) => sorted(i)._2
        case InsertionPoint(hi) if hi <= 0 => min._2
        case InsertionPoint(hi) if hi < sorted.length =>
          val lo = hi - 1
          val interpolateChannel = interpolate(lo, hi)_
          Color(
            red   = interpolateChannel(_.red),
            green = interpolateChannel(_.green),
            blue  = interpolateChannel(_.blue)
          )
        case InsertionPoint(_) => max._2
      }
    }
  }

  def temperaturePixel(
      temperatures: Iterable[(Location, Double)],
      colors: Iterable[(Double, Color)],
      alpha: Int = 255
  )(location: Location): Pixel = {
    val t = predictTemperature(temperatures, location)
    val color = interpolateColor(colors, t)
    Pixel(color.red, color.green, color.blue, alpha)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(
      temperatures: Iterable[(Location, Double)],
      colors: Iterable[(Double, Color)]
    ): Image = {

    val h = 180
    val w = 360
    val pixel = temperaturePixel(temperatures, colors)_

    def i(x: Int, y: Int) = w*y + x
    def location(x: Int, y: Int) = Location(
      lat = -y +  90.0,
      lon =  x - 180.0
    )

    val pixels = new Array[Pixel](h*w)
    for (x <- 0 until w; y <- 0 until h) {
      val px = pixel(location(x, y))
      pixels(i(x, y)) = px
    }

    Image(w, h, pixels)
  }
}

