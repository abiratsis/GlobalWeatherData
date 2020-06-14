package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.SparkSession

private[spark] class TemperatureDataset(val spark: SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = TemperatureDataset.csvSources
  val netCDFSources: Map[String, String] = TemperatureDataset.netCDFSources
  val netCDFFields: Map[String, String] = TemperatureDataset.netCDFFields
}

object TemperatureDataset extends WeatherMetadata{
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: Option[GeoWeatherContext]): TemperatureDataset = context match {
    case Some(ctx) => {
      this.geoWeatherCtx = ctx
      new TemperatureDataset(ctx.spark)
    }
    case None => throw new NullContextException
  }

  val sourceKeys = Set(
    "airTemperatureUrl",
    "skinTemperatureUrl",
    "maxTemperatureUrl",
    "minTemperatureUrl")

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map(
    "airTemperatureUrl" -> "air",
    "skinTemperatureUrl" -> "skt",
    "maxTemperatureUrl" -> "tmax",
    "minTemperatureUrl" -> "tmin"
  ).filterKeys(csvSources.keySet)
}