package com.abiratsis.gweather.config

import java.io.File

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig._
import pureconfig.error.ConfigReaderFailures

case class WeatherTransformations(mergeWinds : Boolean, mergeTemperatures: Boolean)

case class UserSettings (rootDir: String,
                        geoSparkDistance: Int = 1,
                        weatherTransformations : WeatherTransformations = WeatherTransformations(true, true),
                        spark: Map[String, Int] = Map("spark.executor.instances" -> 2, "spark.executor.cores" -> 4),
                        activeSources: List[String] = List("airTemperatureUrl", "minTemperatureUrl", "maxTemperatureUrl",
                                "humidityUrl", "uwindUrl", "vwindUrl", "clearSkyDownwardSolarUrl", "netShortwaveRadiationUrl")
                       ) {
  import com.abiratsis.gweather.common.String._

  require(!isNullOrEmpty(rootDir), "rootDir should be not empty string.")
  require(new File(rootDir).isDirectory, "rootDir should be a valid directory.")
  require(geoSparkDistance >= 1, "geoSparkDistance must be >= 1.")
  require(weatherTransformations != null, "weatherTransformations can't be null.")
  require(spark.nonEmpty && spark.contains("spark.executor.instances"), "spark.executor.instances can't be empty.")
  require(spark.nonEmpty && spark.contains("spark.executor.cores"), "spark.executor.cores can't be empty.")
  require(activeSources.nonEmpty, "activeSources can't be empty.")
}

object UserSettings{
//  def apply(rootDir: String,
//            geoSparkDistance: Int,
//            weatherTransformations: WeatherTransformations,
//            spark: Map[String, Int],
//            activeSources: List[String]): UserSettings =
//    new UserSettings(rootDir, geoSparkDistance, weatherTransformations, spark, activeSources)

  def apply(): UserSettings = {
    val config = ConfigSource.resources("user.conf").load[UserSettings]

    config match {
      case Left(ex : ConfigReaderFailures) => throw new Exception(ex.head.description)
      case Right(c : UserSettings) => c
    }
  }
}