package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.{DataSourceContext, Util}
import com.abiratsis.gweather.spark.weather.{HumidityDataset, SolarDataset, TemperatureDataset, WindDataset}

class WeatherAtLocationHandler(implicit val ctx: DataSourceContext) {
  private def getWeatherByLocation(cols: Seq[String], cmonth :Int, dist: Int) = {
    ctx.spark.sql(s"""
                 | SELECT
                 |        wtb.date, ISO3, country, city, ${cols.mkString(",")}
                 | FROM
                 |     world_tbl clt,
                 |     weather_tbl wtb
                 | WHERE
                 |     month == $cmonth AND ST_Distance(wtb.geom, clt.geom) <= $dist
                  """.stripMargin).cache
  }

  /**
   * Save weather data into the given destination.
   *
   * @param destination
   * @param format One of the {delta, orc, parquet, csv}
   */
  def save(destination: String, format: String = "delta") = {
    val formats = Set("delta", "orc", "parquet", "csv")
    if(!formats.contains(format))
      throw new IllegalArgumentException(s"Format should be one of the:${formats.mkString(",")}")

    val currentDate = java.time.LocalDate.now()

    //todo: add year support for the datasources
    val currentYear = currentDate.getYear
    val currentMonth = currentDate.getMonthValue

    val weatherCols =
      TemperatureDataset.netCDFFields.values ++
      WindDataset.netCDFFields.values ++
      HumidityDataset.netCDFFields.values ++
      SolarDataset.netCDFFields.values

    Util.deleteDir(destination + "/geo_weather")
    for (cmonth <- 1 to currentMonth) {
      val weather_df = getWeatherByLocation(weatherCols.toSeq, cmonth, ctx.conf.global.geoSparkDistance)

      weather_df.write
        .format(format)
        .mode("append")
        .save(destination + "/geo_weather")

    }
  }
}
