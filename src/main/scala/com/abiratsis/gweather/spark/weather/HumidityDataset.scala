package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.SparkSession

private[spark] class HumidityDataset(val spark : SparkSession)
  extends WeatherDataset {

  override val csvSources: Map[String, String] = HumidityDataset.csvSources
  override val netCDFSources: Map[String, String] = HumidityDataset.netCDFSources
  override val netCDFFields: Map[String, String] = HumidityDataset.netCDFFields
}

object HumidityDataset extends WeatherMetadata{
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: GeoWeatherContext): HumidityDataset = {
      this.geoWeatherCtx = context
      new HumidityDataset(context.spark)
  }

  lazy val sourceKeys = Set("humidity")

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map("humidity" -> "shum")
}
