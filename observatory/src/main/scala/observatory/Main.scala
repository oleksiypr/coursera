package observatory

object Main extends App {
  import observatory.Extraction._
  val ts = locateTemperatures(2015, "/stations.csv", "/2015.csv")
  val result = locationYearlyAverageRecords(ts)
  //result foreach println
}
