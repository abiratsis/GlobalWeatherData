package com.abiratsis.gweather.config

import pureconfig._
import pureconfig.generic.auto._

object Config{
  val current = ConfigSource.default.load[Config]
}

case class Config(dataSources: DataSources)

case class DataSources(activeSources: Set[String], sources: DataSource, directories: Directories)

case class DataSource(airTemperatureUrl: String,
                       skinTemperatureUrl: String,
                       maxTemperatureUrl: String,
                       minTemperatureUrl: String,
                       humidityUrl: String,
                       uwindUrl: String,
                       vwindUrl: String,
                       clearSkyDownwardLongWaveUrl: String,
                       clearSkyDownwardSolarUrl: String,
                       downwardLongwaveRadiationUrl: String,
                       downwardSolarRadiationUrl: String,
                       netLongwaveRadiationUrl: String,
                       netShortwaveRadiationUrl: String,
                       worldCountriesUrl: String){

}

case class Directories(temperatureDir: String,
                       humidityDir: String,
                       windDir: String,
                       solarRadiationDir: String,
                       worldDir: String)