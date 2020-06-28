package com.abiratsis.gweather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}
import pureconfig.ConfigSource

import pureconfig._
import pureconfig.generic.auto._

//import org.apache.log4j.{Level, Logger}

object Main extends App {
  //    Logger.getLogger("org")

  val appConfig = ApplicationSettings()
  val userConfig = UserSettings()

  implicit val ctx = Some(GeoWeatherContext(appConfig, userConfig))

  val pipeline = new Pipeline()
  pipeline.execute(ExecStep.instPre, "parquet")
  ctx.head.spark.read.parquet(userConfig.rootDir + "/geo_weather")
    .where("country == 'Greece'")
    .show()
}
