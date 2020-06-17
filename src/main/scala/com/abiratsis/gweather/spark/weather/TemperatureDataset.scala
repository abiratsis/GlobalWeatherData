package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.{DataFrame, SparkSession}

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

  def mergeTemperatures(df : DataFrame) : DataFrame = {
    val spark = this.geoWeatherCtx.spark
    import spark.implicits._

    val tmax_wght = 0.7
    val tmin_wght = 1.0 - tmax_wght

    val maxMinCols = Seq("maxTemperatureUrl", "minTemperatureUrl")
    if (maxMinCols.forall(this.geoWeatherCtx.conf.dataSources.activeSources.contains(_)))
      df.withColumn("temp", ($"tmin" * tmin_wght) + ($"tmax" * tmax_wght))
        .drop("tmin", "tmax")
    else
      df
  }


}