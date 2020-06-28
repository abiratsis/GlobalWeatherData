package com.abiratsis.gweather.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig._
import pureconfig.error.ConfigReaderFailures

case class Directories(temperatureDir: String,
                       humidityDir: String,
                       windDir: String,
                       solarRadiationDir: String,
                       worldDir: String)

case class SourceUrls(airTemperature: String,
                      skinTemperature: String,
                      maxTemperature: String,
                      minTemperature: String,
                      humidity: String,
                      uwind: String,
                      vwind: String,
                      clearSkyDownwardLongWave: String,
                      clearSkyDownwardSolar: String,
                      downwardLongwaveRadiation: String,
                      downwardSolarRadiation: String,
                      netLongwaveRadiation: String,
                      netShortwaveRadiation: String,
                      worldCountries: String)

case class ApplicationSettings (directories: Directories, sourceUrls: SourceUrls) {
  import com.abiratsis.gweather.common.String._

  require(directories != null, "directories can't be null.")
  require(!isNullOrEmpty(directories.temperatureDir), "temperatureDir can't be empty.")
  require(!isNullOrEmpty(directories.windDir), "windDir can't be empty.")
  require(!isNullOrEmpty(directories.humidityDir), "humidityDir can't be empty.")
  require(!isNullOrEmpty(directories.solarRadiationDir), "solarRadiationDir can't be empty.")

  require(sourceUrls != null, "sourceUrls can't be null.")
  require(!isNullOrEmpty(sourceUrls.airTemperature), "airTemperature can't be empty.")
  require(!isNullOrEmpty(sourceUrls.skinTemperature), "skinTemperature can't be empty.")
  require(!isNullOrEmpty(sourceUrls.maxTemperature), "maxTemperature can't be empty.")
  require(!isNullOrEmpty(sourceUrls.minTemperature), "minTemperature can't be empty.")
  require(!isNullOrEmpty(sourceUrls.humidity), "minTemperature can't be empty.")
  require(!isNullOrEmpty(sourceUrls.uwind), "uwind can't be empty.")
  require(!isNullOrEmpty(sourceUrls.vwind), "vwind can't be empty.")
  require(!isNullOrEmpty(sourceUrls.clearSkyDownwardLongWave), "clearSkyDownwardLongWave can't be empty.")
  require(!isNullOrEmpty(sourceUrls.clearSkyDownwardSolar), "clearSkyDownwardSolar can't be empty.")
  require(!isNullOrEmpty(sourceUrls.downwardLongwaveRadiation), "downwardLongwaveRadiation can't be empty.")
  require(!isNullOrEmpty(sourceUrls.downwardSolarRadiation), "downwardSolarRadiation can't be empty.")
  require(!isNullOrEmpty(sourceUrls.netLongwaveRadiation), "netLongwaveRadiation can't be empty.")
  require(!isNullOrEmpty(sourceUrls.netShortwaveRadiation), "netShortwaveRadiation can't be empty.")
  require(!isNullOrEmpty(sourceUrls.worldCountries), "worldCountries can't be empty.")
}

object ApplicationSettings{
//  def apply(directories: Directories, sourceUrls: SourceUrls): ApplicationSettings = {
//    new ApplicationSettings(directories, sourceUrls)
//  }

  def apply(): ApplicationSettings = {
//    val configReader = implicitly(Derivation[ConfigReader[ApplicationSettings]])
    val config = ConfigSource.resources("application.conf").load[ApplicationSettings]
    config match {
      case Left(ex : ConfigReaderFailures) => throw new Exception(ex.head.description)
      case Right(c : ApplicationSettings) => c
    }
  }
}