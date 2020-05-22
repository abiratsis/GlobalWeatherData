package com.abiratsis.gweather.spark

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import java.io.File

class TemperatureDataset(implicit val dsContext : DataSourceContext, implicit val spark: SparkSession)
  extends GeoSpacialDataset {

  override val deltaDestination: String = dsContext.downloadDirs("temperatureDir") + "/merged"

  override val valueColumns: Map[String, String] = Map(
    "airTemperatureUrl" -> "air",
    "skinTemperatureUrl" -> "skt",
    "maxTemperatureUrl" -> "tmax",
    "minTemperatureUrl" -> "tmin"
  )

  override def load(): DataFrame = {
    import implicits._

    val commonCols = Seq("time", "lon", "lat")
    dsContext.temperatureActiveSources.map {
      case (_, v) =>
        spark.read
          .option("header", "true")
          .csv(v)
    }.reduce {
      (df1, df2) =>
        df1.join(df2, commonCols, "inner").drop(commonCols.map(c => df2(c)))
    }.transform {
      toWeatherData(valueColumns.values.toSeq: _*)
    }
  }

  override def cleanUp: Unit = {
    dsContext.temperatureActiveSources.foreach{ case (_, path) => new File(path).delete() }
    dsContext.temperatureActiveCsvSources.foreach{ case (_, path) => new File(path).delete() }
  }
}
