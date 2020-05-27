package com.abiratsis.gweather.spark

import java.io.File

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.functions.{col, expr}
import org.apache.spark.sql.{DataFrame, SparkSession}

private[spark] trait GeoSpacialDataset {

  val dsCtx: DataSourceContext
  val spark: SparkSession
  val csvSources : Map[String, String]

  /**
   * The path where the Delta table will be extracted which is a directory for each weather component
   * i.e /weather/temperature/merged or /weather/humidity/merged
   */
  val deltaDestination: String

  /**
   * Loads the GeoSpacial dataset.
   *
   * @return The dataset
   */
  def load(): DataFrame

  /**
   * Removes .nc and .csv files. The method is called after saveAsDelta has succeeded.
   */
  def cleanUp= csvSources.foreach{ case (_, path) => new File(path).delete() }

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
   *
   * @return A dataframe that contains geo-spacial columns.
   */
  protected def toGeoData(): DataFrame => DataFrame = {
    df =>
      df.withColumn("lon", col("lon").cast("Decimal(24,20)"))
        .withColumn("lat", col("lat").cast("Decimal(24,20)"))
        .withColumn("geom", expr("ST_Point(lon, lat)"))
  }
}
