package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.{GeoWeatherContext, Util}
import com.abiratsis.gweather.spark.{GeoDataset, implicits, weather}
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.functions.{col, lit, month}
import org.apache.spark.sql.types.DoubleType

private[spark] trait WeatherDataset extends GeoDataset {
  val netCDFSources : Map[String, String]
  val netCDFFields : Map[String, String]

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
      toGeoData
    }.transform {
      toWeatherData(netCDFFields.values.toSeq: _*)
    }
  }

  /**
   * Removes .nc and .csv files. The method is called after saveAsDelta has succeeded.
   */
  override def cleanUp(): Unit = {
    super.cleanUp()
    netCDFSources.foreach{ case (_, path) => Util.deleteFile(path) }
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

private[gweather] object WeatherDataset {
  def mergeAndCreateWeatherTable()(implicit ctx: GeoWeatherContext): DataFrame = {
      val tempDf = TemperatureDataset()(ctx).load()
      val humDf = HumidityDataset()(ctx).load()
      val windDf = WindDataset()(ctx).load()
      val solarDf = SolarDataset()(ctx).load()

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

      weatherDf.createOrReplaceTempView("weather_tbl")
      weatherDf
  }

  def toFloat(df: DataFrame): DataFrame = {
    df.schema.fields.filter(_.dataType == DoubleType).foldLeft(df) {
      case (df, c) => df.withColumn(c.name, df(c.name).cast("float"))
    }
  }

  /**
   * Removes missing data (Missing data is flagged with a value of -9.96921e+36f.
   * link: https://psl.noaa.gov/data/gridded/data.ncep.reanalysis.html
   *
   * @param wcols Weather columns
   * @param df Target dataframe
   */
  def cleanMissingData(wcols : List[String])(df: DataFrame): Dataset[Row] = {
    val f = -99692136
    val filterMissingData = wcols.map(col).reduce(_ =!= lit(f) and _ =!= lit(f))
    df.where(filterMissingData)
  }
}

object CDFNumericType extends Enumeration {
  type CDFNumericType = Value
  val double: weather.CDFNumericType.Value = Value("double")
  val float: weather.CDFNumericType.Value = Value("float")
}
