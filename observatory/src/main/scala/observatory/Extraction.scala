package observatory

import java.nio.file.Paths
import java.time.LocalDate
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

case class Station(
    stn: String,
    wban: String,
    latitude: Double,
    longitude: Double
  )

case class Observation(
    stn: String,
    wban: String,
    month: Int,
    day: Int,
    temperature: Double
  )

case class LocalizedObservation(
    stn: String,
    wban: String,
    month: Int,
    day: Int,
    temperature: Double,
    latitude: Double,
    longitude: Double
  )

/**
  * 1st milestone: data extraction
  */
object Extraction {
  import org.apache.log4j.{Level, Logger}
  import org.apache.spark.sql.SparkSession

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  @transient lazy val spark: SparkSession =
    SparkSession
      .builder()
      .appName("ObservatorySession")
      .config("spark.master", "local")
      .getOrCreate()

  import spark.implicits._

  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString

  def read(resource: String): RDD[String] = spark.sparkContext.textFile(fsPath(resource))

  def stationsDs(resource: String): Dataset[Station] = {
    val stationsRdd = read(resource)
      .map(_.split(","))
      .filter { row =>
        row.length == 4 &&
          row(2).nonEmpty &&
          row(3).nonEmpty
      } map { row =>
      Station(
        stn   = row(0),
        wban  = Option(row(1)).filter(_.nonEmpty).getOrElse(""),
        latitude   = row(2).toDouble,
        longitude  = row(3).toDouble
      )
    }

    stationsRdd.toDF.as[Station]
  }

  def observationsDs(resource: String): Dataset[Observation] = {
    val observationsRdd = read(resource)
      .map(_.split(","))
      .filter { row =>
        row.length == 5 &&
          row(2).nonEmpty &&
          row(3).nonEmpty &&
          row(4).nonEmpty
      } map { row=>
        Observation(
          stn   = row(0),
          wban  = Option(row(1)).filter(_.nonEmpty).getOrElse(""),
          month = row(2).toInt,
          day   = row(3).toInt,
          temperature = {
            val fahrenheits = row(4).toDouble
            (fahrenheits - 32.0) / 1.8
          }
        )
      }

    observationsRdd.toDF.as[Observation]
  }

  def localizedObservationsDs(
     observations: Dataset[Observation],
     stations: Dataset[Station]
    ): Dataset[LocalizedObservation] = {

    observations.join(
      stations,
      Seq("stn", "wban")
    ).as[LocalizedObservation].persist()
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

    val obs = observationsDs(temperaturesFile)
    val stns = stationsDs(stationsFile)

    val res =
      localizedObservationsDs(obs, stns) map {
        locObs => (
          (year, locObs.month, locObs.day),
          Location(locObs.latitude, locObs.longitude),
          locObs.temperature
        )
      }

    new Iterable[(LocalDate, Location, Double)] {
      def iterator = new Iterator[(LocalDate, Location, Double)] {
        private[this] val it = res.toLocalIterator()
        def hasNext: Boolean = it.hasNext
        def next(): (LocalDate, Location, Double) = {
          val ((year, month, day), location, temperature) = it.next()
          (LocalDate.of(year, month, day), location, temperature)
        }
      }
    }
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(
      records: Iterable[(LocalDate, Location, Double)]
    ): Iterable[(Location, Double)] = {

    records.groupBy { row =>
      val (_, location, _) = row
      location
    } mapValues { rows =>
      val n = rows.size
      val s = rows.map(_._3).sum
      s / n
    }
  }
}
