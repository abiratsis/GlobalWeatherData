package com.abiratsis.gweather.spark

import java.io.File

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, expr, month}

trait GeoSpacialDataset {
  val dsContext: DataSourceContext
  val spark: SparkSession
  val csvSources : Map[String, String]
  val netCDFSources : Map[String, String]

  /**
   * The path where the Delta table will be extracted which is a directory for each of the weather components
   * i.e /weather/temperature/merged or /weather/humidity/merged
   */
  val deltaDestination: String

  /**
   * The value field in the netCDF file.
   */
  val netCDFFields: Map[String, String]

  /**
   * Loads and joins together all the datasets of one weather component i.e temperature.
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
      toWeatherData(netCDFFields.values.toSeq: _*)
    }
  }
//  "clearSkyDownwardSolarUrl" -> "csdsf"
//  "netShortwaveRadiationUrl" -> "nswrs"

  /**
   * Removes .nc and .csv files. The method is called after saveAsDelta has succeeded.
   */
  def cleanUp: Unit = {
    csvSources.foreach{ case (_, path) => new File(path).delete() }
    netCDFSources.foreach{ case (_, path) => new File(path).delete() }
  }

  /**
   * Save data as Delta table.
   */
  def saveAsDelta(): Unit = {
    this.load.write
      .format("delta")
      .mode("overwrite")
      .save(deltaDestination)

    this.cleanUp
  }

  /**
   * Transforms the underlying dataframe into a GeoSpacial dataframe.
   *
   * @param cols
   * @return A dataframe that contains geo-spacial columns.
   */
  protected def toWeatherData(cols: String*): DataFrame => DataFrame = {
    val wcols = cols.map { c => col(c).cast("double").as(c) }

    df =>
      df.withColumn("lon", col("lon").cast("Decimal(24,20)"))
        .withColumn("lat", col("lat").cast("Decimal(24,20)"))
        .withColumn("geom", expr("ST_Point(lon, lat)"))
        .withColumn("date", col("time").cast("date"))
        .withColumn("month", month(col("date")))
        .select((Seq(col("date"), col("month"), col("geom"), col("lon"), col("lat")) ++ wcols): _*)
        .repartition(col("month"))
        .cache
  }
}
