package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import com.abiratsis.gweather.spark.weather
import org.apache.spark.sql.{DataFrame, SparkSession}

private[spark] class TemperatureDataset(val spark: SparkSession)
  extends WeatherDataset {

  val csvSources: Map[String, String] = TemperatureDataset.csvSources
  val netCDFSources: Map[String, String] = TemperatureDataset.netCDFSources
  val netCDFFields: Map[String, String] = TemperatureDataset.netCDFFields
}

object TemperatureDataset extends WeatherMetadata{
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: GeoWeatherContext): TemperatureDataset = {
      this.geoWeatherCtx = context
      new TemperatureDataset(context.spark)
  }

  val sourceKeys = Set(
    "airTemperature",
    "skinTemperature",
    "maxTemperature",
    "minTemperature")

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map(
    "airTemperature" -> "air",
    "skinTemperature" -> "skt",
    "maxTemperature" -> "tmax",
    "minTemperature" -> "tmin"
  ).filterKeys(csvSources.keySet)

  def mergeMaxMinTemperatures(df : DataFrame) : DataFrame = {
    val tmax_wght = 0.7
    val tmin_wght = 1.0 - tmax_wght
    val maxMinCols = Seq("maxTemperature", "minTemperature")
    if (maxMinCols.forall(this.geoWeatherCtx.userConfig.activeSources.contains(_))) {
      df.withColumn("temp", (df("tmin") * tmin_wght) + (df("tmax") * tmax_wght))
        .drop("tmin", "tmax")
    } else
      df
  }

  def convertToCelcious(df : DataFrame) : DataFrame = {
    netCDFFields.values.foldLeft(df){
      case (df, c) => df.withColumn(c, df(c) - 273.15)
    }
  }
}

object TemperatureScaleType extends Enumeration {
  type TemperatureScaleType = Value
  val celsius: weather.TemperatureScaleType.Value = Value("C")
  val fahrenheit: weather.TemperatureScaleType.Value = Value("F")
}