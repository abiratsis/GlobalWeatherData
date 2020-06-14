package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.SparkSession

private[spark] class SolarDataset(val spark : SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = SolarDataset.csvSources
  val netCDFSources: Map[String, String] = SolarDataset.netCDFSources
  val netCDFFields: Map[String, String] = SolarDataset.netCDFFields
}

object SolarDataset extends WeatherMetadata{
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: Option[GeoWeatherContext]): SolarDataset = context match {
    case Some(ctx) => {
      this.geoWeatherCtx = ctx
      new SolarDataset(ctx.spark)
    }
    case None => throw new NullContextException
  }

  lazy val sourceKeys = Set(
    "clearSkyDownwardLongWaveUrl",
    "clearSkyDownwardSolarUrl",
    "downwardLongwaveRadiationUrl",
    "downwardSolarRadiationUrl",
    "netLongwaveRadiationUrl",
    "netShortwaveRadiationUrl"
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
    "clearSkyDownwardLongWaveUrl" -> "csdlf",
    "clearSkyDownwardSolarUrl" -> "csdsf",
    "downwardLongwaveRadiationUrl" -> "dlwrf",
    "downwardSolarRadiationUrl" -> "dswrf",
    "netLongwaveRadiationUrl" -> "nlwrs",
    "netShortwaveRadiationUrl" -> "nswrs"
  ).filterKeys(csvSources.keySet)
}