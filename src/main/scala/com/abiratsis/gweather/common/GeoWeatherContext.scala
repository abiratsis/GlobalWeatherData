package com.abiratsis.gweather.common

import java.nio.file.Paths

import com.abiratsis.gweather.common.implicits._
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}
import org.apache.spark.sql.SparkSession
import org.datasyslab.geosparksql.utils.GeoSparkSQLRegistrator

class GeoWeatherContext(val appConfig : ApplicationSettings, val userConfig: UserSettings){
  lazy val downloadDirs = Util.ccToMap(appConfig.directories).mapValues{
    case dir : String => Paths.get(userConfig.rootDir, dir).toString
  }
  lazy val downloadSourceUrls = Util.ccToMap(appConfig.sourceUrls)

  lazy val activeDownloadSourceUrls = downloadSourceUrls.filterKeys{k =>
    userConfig.activeSources.contains(k) || k == "worldCountries"
  }

  lazy val activeLocalSources = {
    (downloadSourceUrls.filterKeys(k => userConfig.activeSources.contains(k)) +
      ("worldCountries" -> "worldcities.csv")) join sourcesByDir map{
      case  (k : String, v : Seq[_]) =>
        (k, Paths.get(downloadDirs(v.last.toString), Util.getFileNameFromUrl(v.head.toString)).toString)
    }
  }

  lazy val activeLocalCsvSources = {
    activeLocalSources.mapValues(_ replace (".nc", ".csv"))
  }

  val sourcesByDir = Map(
    "airTemperature" -> "temperatureDir",
    "skinTemperature" -> "temperatureDir",
    "maxTemperature" -> "temperatureDir",
    "minTemperature" -> "temperatureDir",
    "humidity" -> "humidityDir",
    "uwind" -> "windDir",
    "vwind" -> "windDir",
    "clearSkyDownwardLongWave" -> "solarRadiationDir",
    "clearSkyDownwardSolar" -> "solarRadiationDir",
    "downwardLongwaveRadiation" -> "solarRadiationDir",
    "downwardSolarRadiation" -> "solarRadiationDir",
    "netLongwaveRadiation" -> "solarRadiationDir",
    "netShortwaveRadiation" -> "solarRadiationDir",
    "worldCountries" -> "worldDir"
  )

  lazy val spark = SparkSession
    .builder()
    .appName("test")
    .master("local[*]")
    .config("spark.executor.memory", "6g")
    .config("spark.driver.memory", "4g")
    .config("spark.executor.instances", userConfig.spark("spark.executor.instances"))
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.kryo.registrator", "org.datasyslab.geospark.serde.GeoSparkKryoRegistrator")
    .config("geospark.global.index", "true")
    .config("geospark.global.indextype", "quadtree")
    .config("geospark.join.gridtype", "kdbtree")
    .getOrCreate()
}

object GeoWeatherContext {
  def apply(appConfig : ApplicationSettings, userConfig: UserSettings): GeoWeatherContext = {
    val ctx = new GeoWeatherContext(appConfig, userConfig)

    ctx.spark.sparkContext.setLogLevel("WARN")
    GeoSparkSQLRegistrator.registerAll(ctx.spark)

    ctx
  }
}
