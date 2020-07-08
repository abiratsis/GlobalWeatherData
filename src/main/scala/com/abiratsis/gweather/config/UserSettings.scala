package com.abiratsis.gweather.config

import java.io.File

import com.abiratsis.gweather.ExecutionStep
import com.abiratsis.gweather.common.implicits._
import com.abiratsis.gweather.spark.weather.{CDFNumericType, TemperatureScaleType}
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._

case class WeatherTransformations(mergeWinds : Boolean, mergeTemperatures: Boolean)

case class UserSettings (outputDir: String,
                         geoSparkDistance: Int = 1,
                         exportFormat: String = "parquet",
                         weatherTransformations : WeatherTransformations = WeatherTransformations(true, true),
                         spark: Map[String, Int] = Map("spark.executor.instances" -> 2, "spark.executor.cores" -> 4),
                         activeSources: List[String] = List(
                            "airTemperatureUrl", "minTemperatureUrl", "maxTemperatureUrl",
                            "humidityUrl", "uwindUrl", "vwindUrl",
                            "clearSkyDownwardSolarUrl", "netShortwaveRadiationUrl"),
                         startAt: Int = 1,
                         temperatureScale: String = "C",
                         numericType: String = "double"
                       ) {

  import com.abiratsis.gweather.common.String._
  val formats = Set("delta", "orc", "parquet", "csv")
  val tscales: Set[String] = TemperatureScaleType.values.map{_.toString}
  val ntypes: Set[String] = CDFNumericType.values.map{_.toString}

  require(!isNullOrEmpty(outputDir), "rootDir should be non empty string.")
  require(new File(outputDir).isDirectory, "rootDir should be a valid directory.")
  require(geoSparkDistance >= 1, "geoSparkDistance must be >= 1.")
  require(formats.contains(exportFormat), s"Format should be one of the [${formats.mkString(",")}]")
  require(weatherTransformations != null, "weatherTransformations can't be null.")
  require(spark.nonEmpty && spark.contains("spark.executor.instances"), "spark.executor.instances can't be empty.")
  require(spark.nonEmpty && spark.contains("spark.executor.cores"), "spark.executor.cores can't be empty.")
  require(activeSources.nonEmpty, "activeSources can't be empty.")
  require(startAt >= 1 && startAt <= 4, "startAt should be integer in the range 1-4.")
  require(tscales.contains(temperatureScale),
    s"temperatureScale should be one of the [${tscales.mkString(",")}]")
  require(ntypes.contains(numericType), s"numericType should be one of the [${ntypes.mkString(",")}]")

  override def toString: String = {
    s"""
       |User configuration
       |====================
       |rootDir:$outputDir
       |geoSparkDistance: $geoSparkDistance
       |exportFormat: $exportFormat
       |weatherTransformations: $weatherTransformations
       |spark: $spark
       |activeSources: $activeSources
       |startAt: ${ExecutionStep(startAt).toString}
       |temperatureScale: $temperatureScale
       |numericType: $numericType
       |""".stripMargin
  }
}

object UserSettings{
  def apply(): UserSettings = {
    val defaultUserConf = ConfigSource.resources(resourceName).load[UserSettings]

    defaultUserConf match {
      case Left(f : ConfigReaderFailures) => throw new Exception(f.head.description)
      case Right(settings : UserSettings) => settings
    }
  }

  def apply(inputMap: Map[String, String]): UserSettings = {
    val defaultUserConf = ConfigSource.resources(resourceName)
    val currentUserConf = ConfigSource.string(inputMap.toJson).withFallback(defaultUserConf).load[UserSettings]

    currentUserConf match {
      case Left(f : ConfigReaderFailures) => throw new Exception(f.head.description)
      case Right(settings : UserSettings) => settings
    }
  }

  def apply(file: File): UserSettings = {
    val currentUserConf = ConfigSource.file(file).load[UserSettings]

    currentUserConf match {
      case Left(f : ConfigReaderFailures) => throw new Exception(f.head.description)
      case Right(settings : UserSettings) => settings
    }
  }

  val resourceName = "user.conf"
}