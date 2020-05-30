package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

private[spark] class HumidityDataset(val spark : SparkSession)
  extends WeatherDataset {

  override val csvSources: Map[String, String] = HumidityDataset.csvSources
  override val netCDFSources: Map[String, String] = HumidityDataset.netCDFSources
  override val netCDFFields: Map[String, String] = HumidityDataset.netCDFFields
}

object HumidityDataset extends WeatherMetadata{
  var ctx: DataSourceContext = _

  def apply()(implicit dsCtx: DataSourceContext): HumidityDataset = {
    ctx = dsCtx
    new HumidityDataset(dsCtx.spark)
  }

  lazy val sourceKeys = Set("humidityUrl")

  lazy val csvSources: Map[String, String] = ctx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
  lazy val netCDFSources: Map[String, String] =  ctx.activeLocalSources.filterKeys(sourceKeys.contains)

  lazy val netCDFFields: Map[String, String] = Map("humidityUrl" -> "shum")
}
