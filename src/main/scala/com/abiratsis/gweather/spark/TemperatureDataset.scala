package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class TemperatureDataset(implicit val dsContext : DataSourceContext, implicit val spark: SparkSession)
  extends GeoSpacialDataset {
  override val deltaDestination: String = dsContext.downloadDirs("temperatureDir") + "/merged"
  override val csvSources: Map[String, String] = dsContext.temperatureActiveCsvSources
  override val netCDFSources: Map[String, String] = dsContext.temperatureActiveSources

  override val netCDFFields: Map[String, String] = Map(
    "airTemperatureUrl" -> "air",
    "skinTemperatureUrl" -> "skt",
    "maxTemperatureUrl" -> "tmax",
    "minTemperatureUrl" -> "tmin"
  ).filterKeys(csvSources.keySet)
}
