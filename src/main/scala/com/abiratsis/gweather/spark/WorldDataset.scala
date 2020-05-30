package com.abiratsis.gweather.spark
import java.io.File

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.col

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

    new File(s"${WorldDataset.downloadDir}/license.txt").delete()
    new File(s"${WorldDataset.downloadDir}/worldcities.xlsx").delete()
    new File(s"${WorldDataset.downloadDir}/simplemaps_worldcities_basicv1.6.zip").delete()
  }

  def createWorldTable(): Unit = {
    val worldDf = this.load()

    assert(!worldDf.isEmpty, "World dataset can't be empty.")
    worldDf.createOrReplaceTempView("world_tbl")
  }
}

object WorldDataset extends GeoMetadata {
  private var ctx: DataSourceContext = _

  def apply()(implicit dsCtx: DataSourceContext): WorldDataset = {
    ctx = dsCtx
    new WorldDataset(dsCtx.spark)
  }

  lazy val downloadDir = ctx.downloadDirs("worldDir")
  lazy val sourceKeys: Set[String] = Set("worldCountriesUrl")
  lazy val csvSources: Map[String, String] = ctx.activeLocalCsvSources.filterKeys(sourceKeys.contains)
}
