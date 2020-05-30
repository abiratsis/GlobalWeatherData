package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class WeatherAtLocationMatcher(implicit val dsCtx: DataSourceContext, implicit val spark: SparkSession) {
  def getWeatherByLocation(cols: Seq[String], cmonth :Int, dist: Int) = {
    spark.sql(s"""
                 | SELECT
                 |        wtb.date, ISO3, country, city, ${cols.mkString(",")}
                 | FROM
                 |     world_tbl clt,
                 |     weather_tbl wtb
                 | WHERE
                 |     month == $cmonth AND ST_Distance(wtb.geom, clt.geom) <= $dist
                  """.stripMargin).cache
  }


  def save() = {
    val dist = 8
    val currentDate = java.time.LocalDate.now()
    val currentYear = currentDate.getYear
    val currentMonth = currentDate.getMonthValue

    val weatherDataPath = "/tmp/geo_weather"
    val tempDataPath = "/tmp/geo_temp"

    val weatherCols = Seq("air")

    for (cmonth <- 1 to currentMonth) {
      val weather_df = getWeatherByLocation(weatherCols, cmonth, dist)

      weather_df.write
        .format("delta")
        .mode("append")
        .save(weatherDataPath)

    }
  }
}
