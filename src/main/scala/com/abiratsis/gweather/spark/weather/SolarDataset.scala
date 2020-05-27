package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class SolarDataset(implicit val dsCtx : DataSourceContext, implicit val spark: SparkSession)
  extends WeatherDataset {

  override val csvSources: Map[String, String] = dsCtx.solarActiveCsvSources
  override val netCDFSources: Map[String, String] = dsCtx.solarActiveSources

  override val deltaDestination: String = dsCtx.downloadDirs("solarRadiationDir") + "/merged"

  override val netCDFFields: Map[String, String] =  Map(
    "clearSkyDownwardLongWaveUrl" -> "csdlf",
    "clearSkyDownwardSolarUrl" -> "csdsf",
    "downwardLongwaveRadiationUrl" -> "dlwrf",
    "downwardSolarRadiationUrl" -> "dswrf",
    "netLongwaveRadiationUrl" -> "nlwrs",
    "netShortwaveRadiationUrl" -> "nswrs"
  ).filterKeys(csvSources.keySet)
}
