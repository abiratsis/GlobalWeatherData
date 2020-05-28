package com.abiratsis.gweather.spark.weather

import java.io.File

import com.abiratsis.gweather.common.DataSourceContext
import com.abiratsis.gweather.spark.{GeoSpacialDataset, implicits}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, month}

private[spark] trait WeatherDataset extends GeoSpacialDataset {
  val netCDFSources : Map[String, String]

  /**
   * The value field in the netCDF file.
   */
  val netCDFFields: Map[String, String]

  /**
   * Joins together all the datasets of a specific weather component i.e temperature.
   *
   * @return The dataset that contains the joined data.
   */
  def load(): DataFrame = {
    import implicits._

    val commonCols = Seq("time", "lon", "lat")
    this.csvSources.map {
      case (_, v) =>
        spark.read
          .option("header", "true")
          .csv(v)
    }.reduce {
      (df1, df2) =>
        df1.join(df2, commonCols, "inner").drop(commonCols.map(c => df2(c)))
    }.transform {
      toGeoData()
    }.transform {
      toWeatherData(netCDFFields.values.toSeq: _*)
    }
  }

  /**
   * Removes .nc and .csv files. The method is called after saveAsDelta has succeeded.
   */
  override def cleanUp: Unit = {
    super.cleanUp
    netCDFSources.foreach{ case (_, path) => new File(path).delete() }
  }

  /**
   * Transforms the underlying dataframe into a Weather dataframe.
   *
   * @param cols
   * @return A dataframe that contains weather data.
   */
  protected def toWeatherData(cols: String*): DataFrame => DataFrame = {
    val wcols = cols.map { c => col(c).cast("double").as(c) }

    df =>
      df.withColumn("date", col("time").cast("date"))
        .withColumn("month", month(col("date")))
        .select(Seq(col("date"), col("month"), col("geom"), col("lon"), col("lat")) ++ wcols: _*)
        .repartition(col("month"))
        .cache
  }
}

object WeatherDataset {
  def mergeAndCreateWeatherTable(dsCtx: DataSourceContext, spark: SparkSession) = {
    val tempDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("temperatureDir") + "/merged")
      .cache()

    val humDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("humidityDir") + "/merged")
      .cache()

    val windDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("windDir") + "/merged")
      .cache()

    val solarDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("solarRadiationDir") + "/merged")
      .cache()

    val tempCount = tempDf.count()
    assert(tempCount == windDf.count())
    assert(tempCount == humDf.count())
    //assert(tempCount == solarDf.count())

    import com.abiratsis.gweather.spark.implicits._

    val dropCols = Seq("geom", "month")
    val joinCols = Seq("date", "lon", "lat")

    val weatherDf = tempDf.join(windDf, joinCols, "inner")
      .join(solarDf, joinCols, "inner")
      .join(humDf, joinCols, "inner")
      .drop(dropCols.toCol(windDf, solarDf, humDf))
      .cache()

    weatherDf.createOrReplaceGlobalTempView("weather_tbl")
    weatherDf
  }

}
