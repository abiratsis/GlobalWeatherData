package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.SparkSession

private[spark] class WindDataset(val spark : SparkSession)
  extends WeatherDataset {
  val csvSources: Map[String, String] = WindDataset.csvSources
  val netCDFSources: Map[String, String] = WindDataset.netCDFSources
  val netCDFFields: Map[String, String] = WindDataset.netCDFFields
}

object WindDataset extends WeatherMetadata{
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: Option[GeoWeatherContext]): WindDataset = context match {
    case Some(ctx) => {
      this.geoWeatherCtx = ctx
      new WindDataset(ctx.spark)
    }
    case None => throw new NullContextException
  }

  lazy val sourceKeys = Set("uwindUrl", "vwindUrl")

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map(
    "uwindUrl" -> "uwnd",
    "vwindUrl" -> "vwnd"
  ).filterKeys(netCDFSources.keySet)
}
