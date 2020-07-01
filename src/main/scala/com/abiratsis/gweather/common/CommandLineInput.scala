package com.abiratsis.gweather.common
import org.rogach.scallop._
import com.abiratsis.gweather.common.implicits._

class CommandLineInput(args: Seq[String]) extends ScallopConf(args) {
  version("gweather 0.0.1 (c) 2020 abiratsis")
  banner("""Usage: gweather [OPTION]...
           |gweather is a program that allows user to download weather data.
           |Options:
           |""".stripMargin)
  val rootDir = opt[String](required = true, short = 'r', descr = "The root directory where the weather datasources will be exported.")
  val geoSparkDistance = opt[Int](default = Some(1), short = 'd', descr = "The distance between 2 GeoSpark points.")
  val mergeWinds = opt[Boolean](default = Some(true), short = 'w', descr = "A flag specifying whether winds speeds should be merged into one.")
  val mergeTemp = opt[Boolean](default = Some(true), short = 't', descr = "A flag specifying whether min/max temperatures should be merged into one.")
  val exportFormat = opt[String](default = Some("parquet"), short = 'f', descr = "One of the: [delta, orc, parquet, csv].")
  val activeSources = opt[List[String]](short = 'a',default = Some(List("airTemperature", "skinTemperature", "minTemperature",
    "maxTemperature", "humidity", "uwind", "vwind", "clearSkyDownwardSolar", "netShortwaveRadiation")))

  val startAt = choice(choices = List("1", "2", "3", "4"), short = 's', default = Some("1"), descr =
    "The step the process should start from. The available steps are: install prerequisites(1), download data(2), convert to CSV(3), export(4).")

  verify()

  def getInputToMap() : Map[String, String] = {
    val userInputMap = args.sliding(2, 2).map(a => (a.head.last, a.last)).toMap
    val optionsMap = builder.opts.filter{
      p => userInputMap.contains(p.shortNames(0))
    }.map{
      o => (o.shortNames(0), o.name)
    }.toMap

    optionsMap.join(userInputMap).map{case (_,v : Seq[String]) => (v.head, v.last)}
  }
}