package com.abiratsis.gweather.spark
import java.io.File

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.col

class WorldDataset(implicit val dsCtx: DataSourceContext, implicit val spark: SparkSession)
  extends GeoSpacialDataset {

  override val csvSources: Map[String, String] = dsCtx.worldCountriesActiveCsvSources
  override val deltaDestination: String = dsCtx.downloadDirs("worldDir") + "/merged"

  override def load(): DataFrame = {
    val path = this.csvSources.values.head

    spark.read
      .option("header", "true")
      .csv(path)
      .filter(col("lng").isNotNull)
      .withColumnRenamed("lng", "lon")
      .transform(toGeoData())
      .select("city_ascii", "geom", "country", "iso3")
  }

  override def cleanUp: Unit = {
    super.cleanUp

    new File(s"${dsCtx.downloadDirs("worldDir")}/license.txt").delete()
    new File(s"${dsCtx.downloadDirs("worldDir")}/worldcities.xlsx").delete()
    new File(s"${dsCtx.downloadDirs("worldDir")}/simplemaps_worldcities_basicv1.6.zip").delete()
  }
}
