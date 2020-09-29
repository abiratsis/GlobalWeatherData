package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.SparkSession

private[spark] class SolarDataset private(val spark : SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = SolarDataset.csvSources
  val netCDFSources: Map[String, String] = SolarDataset.netCDFSources
  val netCDFFields: Map[String, String] = SolarDataset.netCDFFields
}

private[spark] object SolarDataset extends WeatherMetadata{
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: GeoWeatherContext): SolarDataset = {
      this.geoWeatherCtx = context
      new SolarDataset(context.spark)
  }

  lazy val sourceKeys = Set(
    "clearSkyDownwardLongWave",
    "clearSkyDownwardSolar",
    "downwardLongwaveRadiation",
    "downwardSolarRadiation",
    "netLongwaveRadiation",
    "netShortwaveRadiation"
  )

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map(
    "clearSkyDownwardLongWave" -> "csdlf",
    "clearSkyDownwardSolar" -> "csdsf",
    "downwardLongwaveRadiation" -> "dlwrf",
    "downwardSolarRadiation" -> "dswrf",
    "netLongwaveRadiation" -> "nlwrs",
    "netShortwaveRadiation" -> "nswrs"
  ).filterKeys(csvSources.keySet)
}