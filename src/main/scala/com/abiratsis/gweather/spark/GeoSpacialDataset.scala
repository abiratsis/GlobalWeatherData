package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, expr, month}

trait GeoSpacialDataset {
  val dsContext: DataSourceContext
  val spark: SparkSession
  val valueColumns : Map[String, String]

  def load() : DataFrame
  def saveAsDelta : Unit

  def toWeatherData(cols: String*): DataFrame => DataFrame = {
    val wcols = cols.map{c => col(c).cast("double").as(c)}

    df => df.withColumn("lon", col("lon").cast("Decimal(24,20)"))
      .withColumn("lat", col("lat").cast("Decimal(24,20)"))
      .withColumn("geom", expr("ST_Point(lon, lat)"))
      .withColumn("date", col("time").cast("date"))
      .withColumn("month", month(col("date")))
      .select((Seq(col("date"), col("month"), col("geom"), col("lon"), col("lat")) ++ wcols) :_*)
      .repartition(col("month"))
      .cache
  }
}
