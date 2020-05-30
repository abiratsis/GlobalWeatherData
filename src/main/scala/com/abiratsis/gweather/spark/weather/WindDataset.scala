package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

private[spark] class WindDataset(val spark : SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = WindDataset.csvSources
  val netCDFSources: Map[String, String] = WindDataset.netCDFSources
  val netCDFFields: Map[String, String] = WindDataset.netCDFFields
}

object WindDataset extends WeatherMetadata{
  var ctx: DataSourceContext = _

  def apply()(implicit dsCtx: DataSourceContext): WindDataset = {
    ctx = dsCtx
    new WindDataset(dsCtx.spark)
  }

  lazy val sourceKeys = Set("uwindUrl", "vwindUrl")

  lazy val csvSources: Map[String, String] = ctx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
  lazy val netCDFSources: Map[String, String] =  ctx.activeLocalSources.filterKeys(sourceKeys.contains)

  lazy val netCDFFields: Map[String, String] = Map(
    "uwindUrl" -> "uwnd",
    "vwindUrl" -> "vwnd"
  ).filterKeys(netCDFSources.keySet)
}
