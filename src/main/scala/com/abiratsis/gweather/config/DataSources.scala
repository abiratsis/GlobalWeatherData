package com.abiratsis.gweather.config

import pureconfig._
import pureconfig.generic.auto._

object Config{
  val dataSources = ConfigSource.default.load[DataSources]
}

case class DataSources(airTemperatureSourceUrl: String, airTemperatureDestinationDir: String,
                                       skinTemperatureSourceUrl: String, skinTemperatureDestinationDir: String,
                                       maxTemperatureSourceUrl: String, maxTemperatureDestinationDir: String,
                                       minTemperatureSourceUrl: String, minTemperatureDestinationDir: String,
                                       humiditySourceUrl: String, humidityDestinationDir: String,
                                       uwindSourceUrl: String, uwindDestinationDir: String,
                                       vwindSourceUrl: String, vwindDestinationDir: String,
                                       clearSkyDownwardLongWaveSourceUrl: String, clearSkyDownwardLongWaveDestinationDir: String,
                                       clearSkyDownwardSolarSourceUrl: String, clearSkyDownwardSolarDestinationDir: String,
                                       downwardLongwaveRadiationSourceUrl: String, downwardLongwaveRadiationDestinationDir: String,
                                       downwardSolarRadiationSourceUrl: String, downwardSolarRadiationDestinationDir: String,
                                       netLongwaveRadiationSourceUrl: String, netLongwaveRadiationDestinationDir: String,
                                       netShortwaveRadiationSourceUrl: String, netShortwaveRadiationDestinationDir: String)



