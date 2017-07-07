package observatory

import com.sksamuel.scrimage.Image
import scala.math._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val n = (1 << zoom).toDouble
    val lon = 360.0*(x / n) - 180.0
    val lat = {
      val rad = atan(sinh(Pi * (1.0 - 2.0*y/n)))
      rad.toDegrees
    }
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(
      temperatures: Iterable[(Location, Double)],
      colors: Iterable[(Double, Color)],
      zoom: Int, x: Int, y: Int
    ): Image = {

    val h = 256
    val w = 256

    import Visualization.temperaturePixel
    val pixel = temperaturePixel(temperatures, colors, alpha =  127)_

    def dx(i: Int) = i % w
    def dy(i: Int) = i / w

    val pxs = (0 until h*w).toParArray map { i =>
      pixel(tileLocation(
        zoom + 8,
        x + dx(i),
        y + dy(i))
      )
    }

    Image(w, h, pxs.seq.toArray)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {

    val _ = for {
      (year, data) <- yearlyData
      zoom <- 0 to 3
      x <- 0 until 1 << zoom
      y <- 0 until 1 << zoom
    } {
      generateImage(year, zoom, x, y, data)
    }
  }
}
