package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.{GeoWeatherContext, Util}
import com.abiratsis.gweather.spark.weather.{HumidityDataset, SolarDataset, TemperatureDataset, WindDataset}

class WeatherAtLocationHandler()(implicit val ctx: GeoWeatherContext) {
  private def getWeatherByLocation(cols: Seq[String], cmonth :Int, dist: Int) = {
    val wtCols = cols.map{c => s"wtb.$c"}
    ctx.spark.sql(s"""
                 | SELECT
                 |        wtb.date, clt.ISO3, clt.country, clt.city, ${wtCols.mkString(",")}
                 | FROM
                 |     world_tbl clt,
                 |     weather_tbl wtb
                 | WHERE
                 |     month == $cmonth AND ST_Distance(wtb.geom, clt.geom) <= $dist
                  """.stripMargin).cache
  }

  private def getWeatherByLocation(cols: Seq[String], dist: Int) = {
    val wtCols = cols.map{c => s"wtb.$c"}
    ctx.spark.sql(s"""
                     | SELECT
                     |        wtb.date, clt.ISO3, clt.country, clt.city, ${wtCols.mkString(",")}
                     | FROM
                     |     world_tbl clt,
                     |     weather_tbl wtb
                     | WHERE
                     |     ST_Distance(wtb.geom, clt.geom) <= $dist
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
    if (!formats.contains(format))
      throw new IllegalArgumentException(s"Format should be one of the:${formats.mkString(",")}")

    val currentDate = java.time.LocalDate.now()

    //todo: add year support for the datasources
    val currentYear = currentDate.getYear

    val weatherCols =
        TemperatureDataset.netCDFFields.values ++
        WindDataset.netCDFFields.values ++
        HumidityDataset.netCDFFields.values ++
        SolarDataset.netCDFFields.values

    Util.deleteDir(destination + "geo_weather")
    val weather_df = getWeatherByLocation(weatherCols.toSeq, ctx.conf.global.geoSparkDistance)

    weather_df.write
      .format(format)
      .option("header", "true")
      .mode("overwrite")
      .save(destination + "/geo_weather")
  }
}
