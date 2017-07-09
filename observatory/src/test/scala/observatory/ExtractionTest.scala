package observatory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite}

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite with BeforeAndAfterAll {
  import Extraction._
  import spark.implicits._

  override def afterAll(): Unit = spark.stop()

  test("localizedObsrvations") {

    val stations = spark.sparkContext.parallelize(List(
      Station(id = "724017:03707", latitude = +37.358, longitude = -078.438),
      Station(id = "724017",       latitude = +37.350, longitude = -078.433)
    )).toDS

    val observations = spark.sparkContext.parallelize(List(
      Observation(id = "010013",        month = 11, day = 25, temperature =  4.0),
      Observation(id = "724017",        month =  8, day = 11, temperature = 27.3),
      Observation(id = "724017:03707",  month = 12, day =  6, temperature =  0.0),
      Observation(id = "724017:03707",  month =  1, day = 29, temperature =  2.0)
    )).toDS

    val actual =
      localizedObservations(
        observations,
        stations
      ).collect().toSet

    assert(actual == Set(
      LocalizedObservation("724017:03707", 12,  6,  +0.0, +37.358, -078.438),
      LocalizedObservation("724017:03707",  1, 29,  +2.0, +37.358, -078.438),
      LocalizedObservation("724017",        8, 11, +27.3, +37.350, -078.433)
    ))
  }
}