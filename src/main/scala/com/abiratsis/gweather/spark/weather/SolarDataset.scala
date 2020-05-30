package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

private[spark] class SolarDataset(val spark : SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = SolarDataset.csvSources
  val netCDFSources: Map[String, String] = SolarDataset.netCDFSources
  val netCDFFields: Map[String, String] = SolarDataset.netCDFFields
}

object SolarDataset extends WeatherMetadata{
  var ctx: DataSourceContext = _

  def apply()(implicit dsCtx: DataSourceContext): SolarDataset = {
    ctx = dsCtx
    new SolarDataset(dsCtx.spark)
  }

  lazy val sourceKeys = Set(
    "clearSkyDownwardLongWaveUrl",
    "clearSkyDownwardSolarUrl",
    "downwardLongwaveRadiationUrl",
    "downwardSolarRadiationUrl",
    "netLongwaveRadiationUrl",
    "netShortwaveRadiationUrl"
  )

  lazy val csvSources: Map[String, String] = ctx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
  lazy val netCDFSources: Map[String, String] =  ctx.activeLocalSources.filterKeys(sourceKeys.contains)

  lazy val netCDFFields: Map[String, String] = Map(
    "clearSkyDownwardLongWaveUrl" -> "csdlf",
    "clearSkyDownwardSolarUrl" -> "csdsf",
    "downwardLongwaveRadiationUrl" -> "dlwrf",
    "downwardSolarRadiationUrl" -> "dswrf",
    "netLongwaveRadiationUrl" -> "nlwrs",
    "netShortwaveRadiationUrl" -> "nswrs"
  ).filterKeys(csvSources.keySet)
}