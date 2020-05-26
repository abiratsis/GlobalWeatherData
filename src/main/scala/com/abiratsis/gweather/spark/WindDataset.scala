package com.abiratsis.gweather.spark
import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class WindDataset(implicit val dsContext : DataSourceContext, implicit val spark: SparkSession)
  extends WeatherDataset {
  override val csvSources: Map[String, String] = dsContext.windActiveCsvSources
  override val netCDFSources: Map[String, String] = dsContext.windActiveSources

  override val deltaDestination: String = dsContext.downloadDirs("windDir") + "/merged"

  override val netCDFFields: Map[String, String] = Map(
    "uwindUrl" -> "uwnd",
    "vwindUrl" -> "vwnd"
  ).filterKeys(csvSources.keySet)
}
