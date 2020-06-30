package com.abiratsis.gweather

import com.abiratsis.gweather.common.{CommandLineInput, GeoWeatherContext}
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}

//import org.apache.log4j.{Level, Logger}

object Main extends App {
  //    Logger.getLogger("org")
  val userInput = new CommandLineInput(args)

  val inputMap = userInput.getInputToMap

  val appConfig = ApplicationSettings()
  val userConfig = UserSettings(inputMap, UserSettings.resourceName)

//  println(userConfig)
    implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))

    val pipeline = new Pipeline()
    pipeline.execute(ExecStep.expData, "parquet")
    ctx.head.spark.read.parquet(userConfig.rootDir + "/geo_weather")
      .where("country == 'Greece'")
      .show()
}
