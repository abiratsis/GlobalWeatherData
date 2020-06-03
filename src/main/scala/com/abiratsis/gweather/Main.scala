package com.abiratsis.gweather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.config.Config

//import org.apache.log4j.{Level, Logger}

object Main extends App {
  //    Logger.getLogger("org")

  implicit val conf = Config.current

  conf match {
    case Left(ex) => println(ex)
    case Right(c) => {
      implicit val ctx = GeoWeatherContext(c)

      val pipeline = new Pipeline()
      pipeline.execute(ExecStep.instPre, "parquet")

//      println(ExecStep.withName("instPre"))
    }
  }
}
