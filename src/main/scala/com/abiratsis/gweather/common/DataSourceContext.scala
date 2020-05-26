package com.abiratsis.gweather.common

import com.abiratsis.gweather.common.implicits._
import com.abiratsis.gweather.config.Config

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
  lazy val temperatureSourceKeys = Set(
    "airTemperatureUrl",
    "skinTemperatureUrl",
    "maxTemperatureUrl" ,
    "minTemperatureUrl"
  )

  lazy val temperatureActiveSources = activeLocalSources.filterKeys(temperatureSourceKeys.contains)

  lazy val temperatureActiveCsvSources = activeLocalCsvSources.filterKeys(temperatureSourceKeys.contains)

  /************************* Humidity ************************/

  lazy val humiditySourceKeys = Set("humidityUrl")

  lazy val humidityActiveSources = activeLocalSources.filterKeys(humiditySourceKeys.contains)

  lazy val humidityActiveCsvSources = activeLocalCsvSources.filterKeys(humiditySourceKeys.contains)

  /************************* Wind ************************/

  lazy val windSourceKeys = Set("uwindUrl", "vwindUrl")

  lazy val windActiveSources = activeLocalSources.filterKeys(windSourceKeys.contains)

  lazy val windActiveCsvSources = activeLocalCsvSources.filterKeys(windSourceKeys.contains)

  /************************* Solar ************************/

  lazy val solarSourceKeys = Set(
    "clearSkyDownwardLongWaveUrl",
    "clearSkyDownwardSolarUrl",
    "downwardLongwaveRadiationUrl",
    "downwardSolarRadiationUrl",
    "netLongwaveRadiationUrl",
    "netShortwaveRadiationUrl"
  )

  lazy val solarActiveSources = activeLocalSources.filterKeys(solarSourceKeys.contains)

  lazy val solarActiveCsvSources = activeLocalCsvSources.filterKeys(solarSourceKeys.contains)

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
