package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, expr, month}

trait GeoSpacialDataset {
  val dsContext: DataSourceContext
  val spark: SparkSession
  val deltaDestination: String
  val valueColumns: Map[String, String]

  /**
   * Loads and joins together all the datasets of one weather component i.e temperature.
   *
   * @return The dataset that contains the joined data.
   */
  def load: DataFrame

  /**
   * Removes .nc and .csv files. The method is called when saveAsDelta has succeeded.
   */
  def cleanUp : Unit

  /**
   * Save data as Delta table.
   */
  def saveAsDelta(): Unit =
    this.load.write
      .format("delta")
      .mode("overwrite")
      .save(deltaDestination)

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
