package timeusage

import org.apache.spark.sql.{ColumnName, DataFrame, Row}
import org.apache.spark.sql.types.{
  DoubleType,
  StringType,
  StructField,
  StructType
}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TimeUsageSuite extends FunSuite with BeforeAndAfterAll {
  import TimeUsage._

  test("dfSchema") {
    assert(
      dfSchema(
        List("str_id", "double_1", "double_2")
      ) == StructType(
        List(
          StructField("str_id", StringType, nullable = true),
          StructField("double_1", DoubleType, nullable = true),
          StructField("double_2", DoubleType, nullable = true)
        )
      )
    )
  }


}
