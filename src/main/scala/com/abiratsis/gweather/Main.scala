package com.abiratsis.gweather

import com.abiratsis.gweather.common.{CommandLineInput, GeoWeatherContext}
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}

//todo: fix missing data (Missing data is flagged with a value of -9.96921e+36f.
// link: https://psl.noaa.gov/data/gridded/data.ncep.reanalysis.html
object Main extends App {
  val userInput = new CommandLineInput(args)
  var userConfig: UserSettings = _

  userInput.userConf.toOption match {
    case Some(f) => {
      println(s"Loading configuration from ${f.getCanonicalPath}")
      userConfig = UserSettings(f)
    }
    case None => {
      val inputMap = userInput.getInputToMap
      userConfig = UserSettings(inputMap)

    }
  }

  println(userConfig.toString)
//  System.exit(1)
  val appConfig = ApplicationSettings()

  implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))
//
  val pipeline = new Pipeline()
  pipeline.execute

  ctx.head.spark.read.parquet(userConfig.outputDir + "/export")
    .where("country == 'Greece'")
    .show(1000)
}
