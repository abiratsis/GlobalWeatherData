package com.abiratsis.gweather.config

import java.io.File

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig._
import pureconfig.error.ConfigReaderFailures

import com.abiratsis.gweather.common.implicits._

case class WeatherTransformations(mergeWinds : Boolean, mergeTemperatures: Boolean)

case class UserSettings (rootDir: String,
                          geoSparkDistance: Int = 1,
                          exportFormat: String = "parquet",
                          weatherTransformations : WeatherTransformations = WeatherTransformations(true, true),
                          spark: Map[String, Int] = Map("spark.executor.instances" -> 2, "spark.executor.cores" -> 4),
                          activeSources: List[String] = List(
                            "airTemperatureUrl", "minTemperatureUrl", "maxTemperatureUrl",
                            "humidityUrl", "uwindUrl", "vwindUrl",
                            "clearSkyDownwardSolarUrl", "netShortwaveRadiationUrl")
                       ) {

  import com.abiratsis.gweather.common.String._
  val formats = Set("delta", "orc", "parquet", "csv")

  require(!isNullOrEmpty(rootDir), "rootDir should be not empty string.")
  require(new File(rootDir).isDirectory, "rootDir should be a valid directory.")
  require(geoSparkDistance >= 1, "geoSparkDistance must be >= 1.")
  require(formats.contains(exportFormat), s"Format should be one of the:${formats.mkString(",")}")
  require(weatherTransformations != null, "weatherTransformations can't be null.")
  require(spark.nonEmpty && spark.contains("spark.executor.instances"), "spark.executor.instances can't be empty.")
  require(spark.nonEmpty && spark.contains("spark.executor.cores"), "spark.executor.cores can't be empty.")
  require(activeSources.nonEmpty, "activeSources can't be empty.")
}

object UserSettings{
  def apply(): UserSettings = {
    val defaultUserConf = ConfigSource.resources(resourceName).load[UserSettings]

    defaultUserConf match {
      case Left(f : ConfigReaderFailures) => throw new Exception(f.head.description)
      case Right(settings : UserSettings) => settings
    }
  }

  def apply(inputMap: Map[String, String], resourceName: String): UserSettings = {
    val defaultUserConf = ConfigSource.resources(resourceName)
    val currentUserConf = ConfigSource.string(inputMap.toJson).withFallback(defaultUserConf).load[UserSettings]

    currentUserConf match {
      case Left(f : ConfigReaderFailures) => throw new Exception(f.head.description)
      case Right(settings : UserSettings) => settings
    }
  }

  val resourceName = "user.conf"
}