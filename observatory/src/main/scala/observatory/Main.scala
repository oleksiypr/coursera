package observatory

object Main extends App {
  import observatory.Extraction._
  import observatory.Visualization.visualize

  System.setProperty("hadoop.home.dir", "D:/dev/sdk/hadoop")

  //temperatures.take(10).foreach(println)

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

/*  val temperatures = List(
    (Location(+45.000, +090.000), +32.0),
    (Location(-45.000, +090.000), -50.0),
    (Location(-45.000, -090.000), +32.0),
    (Location(+45.000, -090.000), -50.0)
  )*/

  val locTemps = locateTemperatures(2015, "/stations.csv", "/2015.csv")
  val temperatures = locationYearlyAverageRecords(locTemps)

  val img = visualize(temperatures, colors)
  img.output(new java.io.File("D:/tmp/temperature-2015.png"))
}
