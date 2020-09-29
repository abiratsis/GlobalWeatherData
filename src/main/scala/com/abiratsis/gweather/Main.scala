package com.abiratsis.gweather

import com.abiratsis.gweather.common.{CommandLineInput, GeoWeatherContext}
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}

object Main extends App {

//  val userInput = new CommandLineInput(args)
//  var userConfig: UserSettings = _
//
//  userInput.userConf.toOption match {
//    case Some(f) => {
//      println(s"Loading configuration from ${f.getCanonicalPath}")
//      userConfig = UserSettings(f)
//    }
//    case None => {
//      val inputMap = userInput.getInputToMap
//      userConfig = UserSettings(inputMap)
//
//    }
//  }
//
//  println(userConfig.toString)
////  System.exit(1)
//  val appConfig = ApplicationSettings()
//
//  implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))
//
//  val pipeline = Pipeline()
//  pipeline.execute


//  ctx.head.spark.read.parquet(userConfig.outputDir + "/export")
//    .where("Country == 'Greece'")
//    .show(1000)

  val pipeline = new PipelineBuilder()
    .withParameter("output-dir", "/Users/abiratsis/export_29_09_2020")
    .withParameter("geo-spark-distance", 1)
    .withParameter("export-format", "parquet")
    .withParameter("merge-winds", true)
    .withParameter("active-sources", List("airTemperature", "skinTemperature"))
    .build()

  pipeline.execute()

  pipeline.ctx.spark.read.parquet("/Users/abiratsis/export_29_09_2020")
    .where("Country == 'Greece'")
    .show(1000)
}
