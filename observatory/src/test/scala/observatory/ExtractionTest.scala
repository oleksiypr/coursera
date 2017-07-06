package observatory

import org.apache.spark.rdd.RDD
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite with BeforeAndAfterAll {
  import Extraction._
  //override def afterAll(): Unit = sc.stop()


/*  test("localizedObsrvations") {

    val stations = sc.parallelize(List(
      Station(stn = "724017", wban = "03707", latitude = +37.358, longitude = -078.438),
      Station(stn = "724017", wban = "",          latitude = +37.350, longitude = -078.433)
    ))

    val observations = sc.parallelize(List(
      Observation(stn = "010013", wban = "",          month = 11, day = 25, temperature = 4),
      Observation(stn = "724017", wban = "",          month =  8, day = 11, temperature = 27.3),
      Observation(stn = "724017", wban = "03707", month = 12, day =  6, temperature = 0),
      Observation(stn = "724017", wban = "03707", month =  1, day = 29, temperature = 2)
    ))

    val actual =
      localizedObservations(
        observations,
        stations
      ).collect().toSet

    assert(actual == Set(
      (
        Observation("724017", "03707", 12, 6, 0),
        Station("724017", "03707", +37.358, -078.438)
      ),
      (
        Observation("724017", "03707", 1, 29, 2),
        Station("724017", "03707", +37.358, -078.438)
      ),
      (
        Observation(stn = "724017", "", 8,11, 27.3),
        Station(stn = "724017", "", +37.350, -078.433)
      )
    ))
  }*/
}