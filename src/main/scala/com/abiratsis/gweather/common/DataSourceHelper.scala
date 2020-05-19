package com.abiratsis.gweather.common

import com.abiratsis.gweather.config.Config

class DataSourceHelper(conf : Config){
  val downloadDirs = Util.ccToMap(conf.dataSources.directories)
  val downloadSources = Util.ccToMap(conf.dataSources.sources)

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
    "netShortwaveRadiationUrl" -> "solarRadiationDir"
  )
}

object DataSourceHelper {
  def apply(conf : Config): DataSourceHelper = new DataSourceHelper(conf)
}
