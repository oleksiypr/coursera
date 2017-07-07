package observatory

import scala.concurrent.duration.Duration

object Main extends App {
  import observatory.Extraction._
  import observatory.Visualization.visualize

  import scala.concurrent._
  import ExecutionContext.Implicits.global

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


  private def computeWorldTemperature(year: Int): Future[Unit] = Future  {
    val locTemps = locateTemperatures(year, "/stations.csv", s"/$year.csv")
    val temperatures = locationYearlyAverageRecords(locTemps)

    val img = visualize(temperatures, colors)
    img.output(new java.io.File(s"D:/tmp/temperature-$year.png"))
  }

  val f1 = computeWorldTemperature(1979)
  val f2 = computeWorldTemperature(1990)
  val f3 = computeWorldTemperature(2000)
  val f4 = computeWorldTemperature(2005)

  val res = Future.sequence(List(f1, f2, f3, f4))
  Await.result(res, Duration.Inf)
}
