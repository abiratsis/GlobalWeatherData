package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import org.apache.spark.sql.SparkSession

private[spark] class TemperatureDataset(val spark: SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = TemperatureDataset.csvSources
  val netCDFSources: Map[String, String] = TemperatureDataset.netCDFSources
  val netCDFFields: Map[String, String] = TemperatureDataset.netCDFFields
}

object TemperatureDataset extends WeatherMetadata{
  var ctx: GeoWeatherContext = _

  def apply()(implicit dsCtx: GeoWeatherContext): TemperatureDataset = {
    ctx = dsCtx
    new TemperatureDataset(dsCtx.spark)
  }

  lazy val sourceKeys = Set(
    "airTemperatureUrl",
    "skinTemperatureUrl",
    "maxTemperatureUrl",
    "minTemperatureUrl")

  lazy val csvSources: Map[String, String] = ctx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
  lazy val netCDFSources: Map[String, String] =  ctx.activeLocalSources.filterKeys(sourceKeys.contains)

  lazy val netCDFFields: Map[String, String] = Map(
    "airTemperatureUrl" -> "air",
    "skinTemperatureUrl" -> "skt",
    "maxTemperatureUrl" -> "tmax",
    "minTemperatureUrl" -> "tmin"
  ).filterKeys(csvSources.keySet)
}