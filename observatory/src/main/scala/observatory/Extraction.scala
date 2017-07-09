package observatory

import java.nio.file.Paths
import java.time.LocalDate
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Column, Dataset}
import org.apache.spark.sql.types.{DoubleType, IntegerType}

/**
  * 1st milestone: data extraction
  */
object Extraction {
  import org.apache.log4j.{Level, Logger}
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._

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

  def id: Column = {
    val stn = $"_c0"
    val wban = $"_c1"
    struct(stn, wban).alias("id")
  }

  def stations(resource: String): Dataset[Station] = {
    val stationsDataFrame = spark.read.csv(fsPath(resource))

    stationsDataFrame
      .select(
        id,
        '_c2 as "latitude"  cast DoubleType,
        '_c3 as "longitude" cast DoubleType
      )
      .where(
        'latitude.isNotNull &&
        'longitude.isNotNull
      )
      .as[Station]
  }

  def observations(resource: String): Dataset[Observation] = {
    val observationsDataFrame = spark.read.csv(fsPath(resource))

    val celsius = {
      val fahrenheits = '_c4 as "temperature" cast DoubleType
      (fahrenheits - 32.0) / 1.8
    }

    observationsDataFrame
      .select(
        id,
        '_c2    as "month" cast IntegerType,
        '_c3    as "day"   cast IntegerType,
        celsius as "temperature"
      )
      .where(
        'month.isNotNull &&
        'day.isNotNull &&
        'temperature.isNotNull
      )
      .as[Observation]
  }

  def localizedObservations(
     observations: Dataset[Observation],
     stations: Dataset[Station]
    ): Dataset[LocalizedObservation] = {

    val joined = observations.join(stations, "id")
    joined.as[LocalizedObservation].persist()
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

    val obs = observations(temperaturesFile)
    val stns = stations(stationsFile)

    val res =
      localizedObservations(obs, stns) map {
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
