package com.abiratsis.gweather.common

import com.abiratsis.gweather.common.implicits._
import com.abiratsis.gweather.config.Config
import org.apache.spark.sql.SparkSession
import org.datasyslab.geosparksql.utils.GeoSparkSQLRegistrator

class GeoWeatherContext(val conf : Config){
  lazy val downloadDirs = Util.ccToMap(conf.dataSources.directories)
  lazy val downloadSourceUrls = Util.ccToMap(conf.dataSources.sources)

  lazy val activeDownloadSourceUrls = downloadSourceUrls.filterKeys( k => conf.dataSources.activeSources.contains(k) || k == "worldCountriesUrl")

  lazy val activeLocalSources = {
    (downloadSourceUrls.filterKeys(k => conf.dataSources.activeSources.contains(k)) +
      ("worldCountriesUrl" -> "worldcities.csv")) join sourcesByDir map{
      case  (k : String, v : Seq[_]) =>
        (k, downloadDirs(v.last.toString) + "/" + Util.getFileNameFromUrl(v.head.toString))
    }
  }

  lazy val activeLocalCsvSources = {
    activeLocalSources.mapValues(_ replace (".nc", ".csv"))
  }

  val sourcesByDir = Map(
    "airTemperatureUrl" -> "temperatureDir",
    "skinTemperatureUrl" -> "temperatureDir",
    "maxTemperatureUrl" -> "temperatureDir",
    "minTemperatureUrl" -> "temperatureDir",
    "humidityUrl" -> "humidityDir",
    "uwindUrl" -> "windDir",
    "vwindUrl" -> "windDir",
    "clearSkyDownwardLongWaveUrl" -> "solarRadiationDir",
    "clearSkyDownwardSolarUrl" -> "solarRadiationDir",
    "downwardLongwaveRadiationUrl" -> "solarRadiationDir",
    "downwardSolarRadiationUrl" -> "solarRadiationDir",
    "netLongwaveRadiationUrl" -> "solarRadiationDir",
    "netShortwaveRadiationUrl" -> "solarRadiationDir",
    "worldCountriesUrl" -> "worldDir"
  )

  lazy val spark = SparkSession
    .builder()
    .appName("test")
    .master("local[*]")
    .config("spark.executor.memory", "6g")
    .config("spark.driver.memory", "4g")
    .config("spark.executor.instances", conf.global.spark("spark.executor.instances"))
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.kryo.registrator", "org.datasyslab.geospark.serde.GeoSparkKryoRegistrator")
    .config("geospark.global.index", "true")
    .config("geospark.global.indextype", "quadtree")
    .config("geospark.join.gridtype", "kdbtree")
    .getOrCreate()
}

object GeoWeatherContext {
  def apply(conf : Config): GeoWeatherContext = {
    val ctx = new GeoWeatherContext(conf)

    ctx.spark.sparkContext.setLogLevel("WARN")
    GeoSparkSQLRegistrator.registerAll(ctx.spark)

    ctx
  }
}
