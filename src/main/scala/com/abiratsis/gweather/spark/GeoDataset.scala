package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.Util
import org.apache.spark.sql.functions.{col, expr}
import org.apache.spark.sql.{DataFrame, SparkSession}

private trait GeoDataset {
  val spark: SparkSession
  val csvSources : Map[String, String]

  /**
   * Loads the GeoSpacial dataset.
   *
   * @return The dataset
   */
  def load(): DataFrame

  /**
   * Removes .nc and .csv files. The method is called after saveAsDelta has succeeded.
   */
  def cleanUp= csvSources.foreach{ case (_, path) => Util.deleteFile(path) }

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
