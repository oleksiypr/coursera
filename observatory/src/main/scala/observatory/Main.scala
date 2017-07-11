package observatory

import scala.concurrent.duration.Duration

object Main extends App {
  import Extraction._
  import Interaction._
  import observatory.Visualization.visualize

  System.setProperty("hadoop.home.dir", "D:/dev/sdk/hadoop")

  val colors = List(
    (+60.0, Color(255,  255,  255)),
    (+32.0, Color(255,    0,    0)),
    (+12.0, Color(255,  255,    0)),
    (  0.0, Color(  0,  255,  255)),
    (-15.0, Color(  0,    0,  255)),
    (-27.0, Color(255,    0,  255)),
    (-50.0, Color( 33,    0,  107)),
    (-60.0, Color(  0,    0,    0))
  )

  type TemperatureData =  Iterable[(Location, Double)]


  //val locTemps = locateTemperatures(2015, "/stations.csv", "/2015.csv")
  //val temperatures: Iterable[(Location, Double)] = locationYearlyAverageRecords(locTemps)
  //val yearlyData: Iterable[(Int, TemperatureData)] = List((2015, temperatures))

  /**  “target/temperatures/2015/<zoom>/<x>-<y>.png”
    * Where “<zoom>” is replaced by the zoom level, and “<x>” and “<y>” are replaced by
    * the tile coordinates. For instance, the tile located at coordinates (0, 1),
    * for the zoom level 1 will have to be located in the following file: “target/temperatures/2015/1/0-1.png”.
    */
  def generateImage(
    year: Int,
    zoom: Int,
    x: Int, y: Int,
    temperatures: Iterable[(Location, Double)]): Unit = {

    val path = s"target/temperatures/$year/$zoom/$x-$y.png"
    val img = tile(temperatures, colors, zoom, x, y)
    img.output(new java.io.File(path))
  }

  val yearlyData: Iterable[(Int, TemperatureData)] =
    List((2015, List(
      (Location(+45.000, +090.000), +30.0),
      (Location(-45.000, +090.000), -20.0),
      (Location(-45.000, -090.000), +30.0),
      (Location(+45.000, -090.000), -30.0)
    )))

  //Interaction.generateTiles(yearlyData, generateImage)
  Interaction.generateTiles(yearlyData, generateImage)
}
