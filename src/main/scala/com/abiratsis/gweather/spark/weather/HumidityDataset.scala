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

  def apply()(implicit context: Option[GeoWeatherContext]): HumidityDataset = context match {
    case Some(ctx) => {
      this.geoWeatherCtx = ctx
      new HumidityDataset(ctx.spark)
    }
    case None => throw new NullContextException
  }

  lazy val sourceKeys = Set("humidityUrl")

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map("humidityUrl" -> "shum")
}
