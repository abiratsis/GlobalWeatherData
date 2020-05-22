package com.abiratsis.gweather.common

import com.abiratsis.gweather.config.Config

import implicits._
class DataSourceContext(conf : Config){
  lazy val downloadDirs = Util.ccToMap(conf.dataSources.directories)
  lazy val downloadSources = Util.ccToMap(conf.dataSources.sources)

  lazy val activeLocalSources = {
    (downloadSources.filterKeys(k => conf.dataSources.activeSources.contains(k)) +
      ("worldCountriesUrl" -> "worldcities.csv")) join sourcesByDir map{
      case  (k : String, v : Seq[Any]) =>
        (k, downloadDirs(v(1).toString).toString + "/" + Util.getFileNameFromUrl(v(0).toString) replace (".nc", ".csv"))
    }
  }

  lazy val temperatureSourceKeys = Seq(
    "airTemperatureUrl",
    "skinTemperatureUrl",
    "maxTemperatureUrl" ,
    "minTemperatureUrl"
  )

  lazy val temperatureActiveSources = {
    activeLocalSources.filterKeys(temperatureSourceKeys.contains(_))
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
}

object DataSourceContext {
  def apply(conf : Config): DataSourceContext = new DataSourceContext(conf)
}
