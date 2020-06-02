package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.{GeoWeatherContext, Util}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}

private[spark] class WorldDataset(val spark: SparkSession)
  extends GeoDataset {

  val csvSources: Map[String, String] = WorldDataset.csvSources

  override def load(): DataFrame = {
    val path = this.csvSources.values.head

    spark.read
      .option("header", "true")
      .csv(path)
      .filter(col("lng").isNotNull)
      .withColumnRenamed("lng", "lon")
      .transform(toGeoData())
      .select("city_ascii", "geom", "country", "iso3")
      .withColumnRenamed("city_ascii", "city")
  }

  override def cleanUp: Unit = {
    super.cleanUp

    Util.deleteFile(s"${WorldDataset.downloadDir}/license.txt")
    Util.deleteFile(s"${WorldDataset.downloadDir}/worldcities.xlsx")
    Util.deleteFile(s"${WorldDataset.downloadDir}/simplemaps_worldcities_basicv1.6.zip")
  }

  def createWorldTable(): Unit = {
    val worldDf = this.load()

    assert(!worldDf.isEmpty, "World dataset can't be empty.")
    worldDf.createOrReplaceTempView("world_tbl")
  }
}

object WorldDataset extends GeoMetadata {
  private var ctx: GeoWeatherContext = _

  def apply()(implicit dsCtx: GeoWeatherContext): WorldDataset = {
    ctx = dsCtx
    new WorldDataset(dsCtx.spark)
  }

  lazy val downloadDir = ctx.downloadDirs("worldDir")
  lazy val sourceKeys: Set[String] = Set("worldCountriesUrl")
  lazy val csvSources: Map[String, String] = ctx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
}
