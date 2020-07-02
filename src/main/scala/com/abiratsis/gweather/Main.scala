package com.abiratsis.gweather

import com.abiratsis.gweather.common.{CommandLineInput, GeoWeatherContext}
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}

//import org.apache.log4j.{Level, Logger}

object Main extends App {
  //    Logger.getLogger("org")
  val userInput = new CommandLineInput(args)

  //  println( userInput.summary)

  val inputMap = userInput.getInputToMap

  val appConfig = ApplicationSettings()
  val userConfig = UserSettings(inputMap, UserSettings.resourceName)

  implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))

  val pipeline = new Pipeline()
  pipeline.execute

  ctx.head.spark.read.parquet(userConfig.rootDir + "/geo_weather")
    .where("country == 'Greece'")
    .show(1000)
}
