package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class HumidityDataset(implicit val dsCtx : DataSourceContext, implicit val spark: SparkSession)
  extends WeatherDataset {
  override val deltaDestination: String = dsCtx.downloadDirs("humidityDir") + "/merged"
  override val csvSources: Map[String, String] = dsCtx.humidityActiveCsvSources
  override val netCDFSources: Map[String, String] = dsCtx.humidityActiveSources

  override val netCDFFields: Map[String, String] = Map(
    "humidityUrl" -> "shum"
  ).filterKeys(csvSources.keySet)
}
