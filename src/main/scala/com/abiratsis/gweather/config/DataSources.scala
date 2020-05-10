package com.abiratsis.gweather.config

import pureconfig._
import pureconfig.generic.auto._

object Config{
  val current = ConfigSource.default.load[Config]
}

case class Config(dataSources: DataSources)

case class DataSources(activeResources: Seq[String], resources: Resources, directories: Directories)

case class Resources(airTemperatureUrl: String,
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
                       netShortwaveRadiationUrl: String)

case class Directories(temperatureDir: String,
                       humidityDir: String,
                       windDir: String,
                       solarRadiationDir: String)
