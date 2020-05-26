package com.abiratsis.gweather.spark
import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}

class HumidityDataset(implicit val dsContext : DataSourceContext, implicit val spark: SparkSession)
  extends GeoSpacialDataset {
  override val deltaDestination: String = dsContext.downloadDirs("humidityDir") + "/merged"
  override val csvSources: Map[String, String] = dsContext.humidityActiveCsvSources
  override val netCDFSources: Map[String, String] = dsContext.humidityActiveSources

  override val netCDFFields: Map[String, String] = Map(
    "humidityUrl" -> "shum"
  ).filterKeys(csvSources.keySet)
}
