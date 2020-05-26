package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.SparkSession

class SolarDataset(implicit val dsContext : DataSourceContext, implicit val spark: SparkSession)
  extends WeatherDataset {

  override val csvSources: Map[String, String] = dsContext.solarActiveCsvSources
  override val netCDFSources: Map[String, String] = dsContext.solarActiveSources

  override val deltaDestination: String = dsContext.downloadDirs("solarRadiationDir") + "/merged"

  override val netCDFFields: Map[String, String] =  Map(
    "clearSkyDownwardLongWaveUrl" -> "csdlf",
    "clearSkyDownwardSolarUrl" -> "csdsf",
    "downwardLongwaveRadiationUrl" -> "dlwrf",
    "downwardSolarRadiationUrl" -> "dswrf",
    "netLongwaveRadiationUrl" -> "nlwrs",
    "netShortwaveRadiationUrl" -> "nswrs"
  ).filterKeys(csvSources.keySet)
}
