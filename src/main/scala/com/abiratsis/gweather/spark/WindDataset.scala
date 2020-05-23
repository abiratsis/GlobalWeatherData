package com.abiratsis.gweather.spark
import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class WindDataset(implicit val dsContext : DataSourceContext, implicit val spark: SparkSession) extends GeoSpacialDataset {
  override val csvSources: Map[String, String] = dsContext.windActiveCsvSources
  override val netCDFSources: Map[String, String] = dsContext.windActiveSources

  /**
   * The path where the Delta table will be extracted which is a directory for each of the weather components
   * i.e /weather/temperature/merged or /weather/humidity/merged
   */
  override val deltaDestination: String = dsContext.downloadDirs("windDir") + "/merged"

  /**
   * The value field in the netCDF file.
   */
  override val netCDFFields: Map[String, String] = Map(
    "uwindUrl" -> "uwnd",
    "vwindUrl" -> "vwnd"
  )
}
