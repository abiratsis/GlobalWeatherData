package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class TemperatureDataset(implicit val dsCtx : DataSourceContext, implicit val spark: SparkSession)
  extends WeatherDataset {
  override val deltaDestination: String = dsCtx.downloadDirs("temperatureDir") + "/merged"
  override val csvSources: Map[String, String] = dsCtx.temperatureActiveCsvSources
  override val netCDFSources: Map[String, String] = dsCtx.temperatureActiveSources

  override val netCDFFields: Map[String, String] = Map(
    "airTemperatureUrl" -> "air",
    "skinTemperatureUrl" -> "skt",
    "maxTemperatureUrl" -> "tmax",
    "minTemperatureUrl" -> "tmin"
  ).filterKeys(csvSources.keySet)
}
