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

  override def afterAll(): Unit = spark.stop()

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

  test("classifiedColumns") {
    assert(classifiedColumns(List("t01123", "t03512","t051239", "t1251")) ===
      (
        List(new ColumnName("t01123"), new ColumnName("t03512")),
        List(new ColumnName("t051239")),
        List(new ColumnName("t1251")))
      )
  }
}
