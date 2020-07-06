package com.abiratsis.gweather

import com.abiratsis.gweather.common.{CommandLineInput, GeoWeatherContext}
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}

//import org.apache.log4j.{Level, Logger}
//todo: fix missing data (Missing data is flagged with a value of -9.96921e+36f.
// link: https://psl.noaa.gov/data/gridded/data.ncep.reanalysis.html
object Main extends App {
  //    Logger.getLogger("org")
  val userInput = new CommandLineInput(args)

  //    println( userInput.summary)

  var userConfig: UserSettings = _

  userInput.userConf.toOption match {
    case Some(f) => {
      userConfig = UserSettings(f)
    }
    case None => {
      val inputMap = userInput.getInputToMap
      userConfig = UserSettings(inputMap)
    }
  }

//  println(userConfig)
//  System.exit(1)
  val appConfig = ApplicationSettings()

  implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))

  val pipeline = new Pipeline()
  pipeline.execute

  ctx.head.spark.read.parquet(userConfig.rootDir + "/geo_weather")
    .where("country == 'Greece'")
    .show(1000)
}
