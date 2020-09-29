package com.abiratsis.gweather.spark

import java.nio.file.Paths

import com.abiratsis.gweather.common.{GeoWeatherContext, Util}
import com.abiratsis.gweather.spark.weather.{CDFNumericType, HumidityDataset, SolarDataset, TemperatureDataset, TemperatureScaleType, WeatherDataset, WindDataset}

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

    val exportPath = Paths.get(destination, "/export").toString
    Util.deleteDir(exportPath)

    var weatherDf = getWeatherByLocation(weatherCols.toSeq, ctx.userConfig.geoSparkDistance)
    if (ctx.userConfig.temperatureScale == TemperatureScaleType.celsius.toString)
      weatherDf = weatherDf.transform(TemperatureDataset.convertToCelcious)

    if (ctx.userConfig.mergeWinds)
      weatherDf = weatherDf.transform(WindDataset.mergeWindSpeed)

    if (ctx.userConfig.mergeTemperatures)
      weatherDf = weatherDf.transform(TemperatureDataset.mergeMaxMinTemperatures)

    if (ctx.userConfig.numericType == CDFNumericType.float.toString)
      weatherDf = weatherDf.transform(WeatherDataset.toFloat)

    weatherDf
      .transform(WeatherDataset.cleanMissingData(weatherCols.toList))
      .write
      .format(format)
      .option("header", "true")
      .mode("overwrite")
      .save(exportPath)
  }
}
