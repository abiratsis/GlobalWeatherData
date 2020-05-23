package com.abiratsis.gweather.common

import com.abiratsis.gweather.config.Config

import implicits._
class DataSourceContext(conf : Config){
  lazy val downloadDirs = Util.ccToMap(conf.dataSources.directories)
  lazy val downloadSourceUrls = Util.ccToMap(conf.dataSources.sources)

  lazy val activeDownloadSourceUrls = downloadSourceUrls.filterKeys( k => conf.dataSources.activeSources.contains(k) || k == "worldCountriesUrl")

  lazy val activeLocalSources = {
    (downloadSourceUrls.filterKeys(k => conf.dataSources.activeSources.contains(k)) +
      ("worldCountriesUrl" -> "worldcities.csv")) join sourcesByDir map{
      case  (k : String, v : Seq[Any]) =>
        (k, downloadDirs(v(1).toString).toString + "/" + Util.getFileNameFromUrl(v(0).toString))
    }
  }

  lazy val activeLocalCsvSources = {
    activeLocalSources.mapValues(_ replace (".nc", ".csv"))
  }

  /************************* Temperature ************************/
  lazy val temperatureSourceKeys = Seq(
    "airTemperatureUrl",
    "skinTemperatureUrl",
    "maxTemperatureUrl" ,
    "minTemperatureUrl"
  )

  lazy val temperatureActiveSources = activeLocalSources.filterKeys(temperatureSourceKeys.contains(_))

  lazy val temperatureActiveCsvSources = activeLocalCsvSources.filterKeys(temperatureSourceKeys.contains(_))

  /************************* Humidity ************************/

  lazy val humiditySourceKeys = Seq("humidityUrl")

  lazy val humidityActiveSources = activeLocalSources.filterKeys(humiditySourceKeys.contains(_))

  lazy val humidityActiveCsvSources = activeLocalCsvSources.filterKeys(humiditySourceKeys.contains(_))

  /************************* Wind ************************/

  lazy val windSourceKeys = Seq("uwindUrl", "vwindUrl")

  lazy val windActiveSources = activeLocalSources.filterKeys(windSourceKeys.contains(_))

  lazy val windActiveCsvSources = activeLocalCsvSources.filterKeys(windSourceKeys.contains(_))

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
