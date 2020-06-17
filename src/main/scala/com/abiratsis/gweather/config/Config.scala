package com.abiratsis.gweather.config

import pureconfig._
import pureconfig.generic.auto._

object Config{
  val current = ConfigSource.default.load[Config]
}

case class Config(global: Global, dataSources: DataSources)

case class Global(rootDir: String,
                  geoSparkDistance: Int,
                  weatherTransformations : Map[String, Boolean],
                  spark: Map[String, Int])

case class Transformations(mergeWinds : Boolean)

case class DataSources(activeSources: Set[String],
                        sources: DataSource,
                        directories: Directories)

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
                       worldCountriesUrl: String)

case class Directories(temperatureDir: String,
                       humidityDir: String,
                       windDir: String,
                       solarRadiationDir: String,
                       worldDir: String)