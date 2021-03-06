package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{pow, sqrt}

private[spark] class WindDataset private(protected val spark : SparkSession)
  extends WeatherDataset{
  val csvSources: Map[String, String] = WindDataset.csvSources
  val netCDFSources: Map[String, String] = WindDataset.netCDFSources
  val netCDFFields: Map[String, String] = WindDataset.netCDFFields
}

private[spark] object WindDataset extends WeatherMetadata {
  var geoWeatherCtx: GeoWeatherContext = _

  def apply()(implicit context: GeoWeatherContext): WindDataset = {
      this.geoWeatherCtx = context
      new WindDataset(context.spark)
  }

  lazy val sourceKeys = Set("uwind", "vwind")

  lazy val csvSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFSources: Map[String, String] = Option(geoWeatherCtx) match {
    case Some(_) => geoWeatherCtx.activeLocalSources.filterKeys(sourceKeys.contains)
    case None => throw new NullContextException
  }

  lazy val netCDFFields: Map[String, String] = Map(
    "uwind" -> "uwnd",
    "vwind" -> "vwnd"
  ).filterKeys(netCDFSources.keySet)

  /**
   * If mergeWinds is true merges vwnd and uwnd into one using the formula ws = sqrt(u^2+v^2),
   * speed is calculated as m/sec.
   *
   * Please check http://colaweb.gmu.edu/dev/clim301/lectures/wind/wind-uv for more details.
   *
   * @return The wind dataset
   */
  def mergeWindSpeed(df: DataFrame) : DataFrame = {
    val spark = this.geoWeatherCtx.spark
    import spark.implicits._

    val windCols = Seq("vwind", "uwind")
    if (windCols.forall(this.geoWeatherCtx.userConfig.activeSources.contains(_)))
      df.withColumn("wind_speed", sqrt(pow($"vwnd", 2.0) + pow($"uwnd", 2.0)))
        .drop("vwnd", "uwnd")
    else
      df
  }
}
