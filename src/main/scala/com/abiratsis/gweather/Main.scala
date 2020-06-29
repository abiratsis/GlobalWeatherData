package com.abiratsis.gweather

import com.abiratsis.gweather.common.{CommandLineInput, GeoWeatherContext}
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._
import com.abiratsis.gweather.common.implicits._

//import org.apache.log4j.{Level, Logger}

object Main extends App {
  //    Logger.getLogger("org")

  val userInput = new CommandLineInput(args)

//  userInput.printHelp()
  val userInputMap = userInput.args.sliding(2, 2).map(a => (a.head.last, a.last)).toMap
  val optionsMap = userInput.builder.opts.filter{
    p => userInputMap.contains(p.shortNames(0))
  }.map{
    o => (o.shortNames(0), o.name)
  }.toMap

  val mergedInput = optionsMap.join(userInputMap).map{case (_,v) => (v.head, v.last)}

  println(mergedInput)

  //  val appConfig = ApplicationSettings()
  //  val userConfig = UserSettings()
  //
  //  implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))
  //
  //  val pipeline = new Pipeline()
  //  pipeline.execute(ExecStep.instPre, "parquet")
  //  ctx.head.spark.read.parquet(userConfig.rootDir + "/geo_weather")
  //    .where("country == 'Greece'")
  //    .show()
}
