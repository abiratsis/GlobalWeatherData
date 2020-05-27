package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class WindDataset(implicit val dsCtx : DataSourceContext, implicit val spark: SparkSession)
  extends WeatherDataset {
  override val csvSources: Map[String, String] = dsCtx.windActiveCsvSources
  override val netCDFSources: Map[String, String] = dsCtx.windActiveSources

  override val deltaDestination: String = dsCtx.downloadDirs("windDir") + "/merged"

  override val netCDFFields: Map[String, String] = Map(
    "uwindUrl" -> "uwnd",
    "vwindUrl" -> "vwnd"
  ).filterKeys(csvSources.keySet)
}
