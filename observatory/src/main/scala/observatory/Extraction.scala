package observatory

import java.time.LocalDate

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

case class Station(
    stn: String,
    wban: Option[String],
    lat: Double,
    long: Double
  )

case class Observation(
    stn: String,
    wban: Option[String],
    month: Int,
    day: Int,
    temperature: Double
  )

/**
  * 1st milestone: data extraction
  */
object Extraction {
  import org.apache.log4j.{Level, Logger}
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  @transient lazy val conf: SparkConf = new SparkConf()
    .setMaster("local")
    .setAppName("StackOverflow")

  @transient lazy val sc: SparkContext = new SparkContext(conf)

  def stations(resource: String): RDD[Station] = {
    sc.textFile(getClass.getResource(resource).getPath)
      .map(_.split(","))
      .filter { row =>
        row.length >= 3 &&
        row(2).nonEmpty &&
        row(3).nonEmpty
      }
      .map { row =>
        Station(
          stn   = row(0),
          wban  = Option(row(1)).filter(_.nonEmpty),
          lat   = row(2).toDouble,
          long  = row(3).toDouble
        )
      }
  }

  def observation(resource: String): RDD[Observation] = {
    sc.textFile(getClass.getResource(resource).getPath)
      .map(_.split(","))
      .filter { row =>
        row.length >= 4 &&
        row(2).nonEmpty &&
        row(3).nonEmpty &&
        row(4).nonEmpty
      }
      .map { row=>
        Observation(
          stn   = row(0),
          wban  = Option(row(1)).filter(_.nonEmpty),
          month = row(2).toInt,
          day   = row(3).toInt,
          temperature = {
            val fahrenheits = row(4).toDouble
            (fahrenheits - 32.0) / 1.8
          }
        )
      }
  }

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(
      year: Int,
      stationsFile: String,
      temperaturesFile: String
    ): Iterable[(LocalDate, Location, Double)] = {


    ???
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    ???
  }

}
