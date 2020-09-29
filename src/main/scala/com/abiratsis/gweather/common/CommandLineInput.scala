package com.abiratsis.gweather.common
import java.io.File

import org.rogach.scallop._
import com.abiratsis.gweather.common.implicits._

// Resources:
// https://stackoverflow.com/questions/23242493/linux-unix-man-page-syntax-conventions
// https://stackoverflow.com/questions/21503865/how-to-denote-that-a-command-line-argument-is-optional-when-printing-usage
private[gweather] class CommandLineInput(args: Seq[String]) extends ScallopConf(args) {
  version("gweather 0.1.0 (c) 2020 abiratsis")
  banner("""Usage: gweather -m <input_mode> [-r <root_directory> [processing options, export options] | --user-conf <user_conf_file>]
           |Processing options: [-s start_at], [-a active_sources], [-d geo_distance], [-w merge_winds], [-t merge_temp]
           |Export options: [-f export_format], [-l temperature_scale], [-n numeric_type]
           |
           |gweather is a program that allows user to download and process weather data.
           |
           |Options:
           |""".stripMargin)

  val outputDir = opt[String](short = 'o', descr = "The output directory where the weather datasources will be exported.")
  val geoSparkDistance = opt[Int](default = Some(1), short = 'd', descr = "The distance between 2 GeoSpark points.")
  val mergeWinds = opt[Boolean](default = Some(true), short = 'w', descr = "A flag specifying whether winds speeds should be merged into one.")
  val mergeTemp = opt[Boolean](default = Some(true), short = 't', descr = "A flag specifying whether min/max temperatures should be merged into one.")
  val exportFormat = opt[String](default = Some("parquet"), short = 'f', descr = "Type of exported data, it should be one of [delta, orc, parquet, csv].")
  val temperatureScale = opt[String](default = Some("C"), short = 'l', descr = "Temperature scale, it should be one of [C, F].")
  val numericType = opt[String](default = Some("double"), short = 'n', descr = "The numeric type for CDF columns, it should be one of [double, float].")

  val activeSources = opt[List[String]](short = 'a',
    default = Some(List("airTemperature", "skinTemperature", "minTemperature", "maxTemperature", "humidity", "uwind", "vwind", "clearSkyDownwardSolar", "netShortwaveRadiation")),
    descr = "The list of netCDF data-sources to download."
  )

  val startAt = choice(choices = List("1", "2", "3", "4"), short = 's', default = Some("1"), descr =
    "The step the process should start from. The available steps are: install prerequisites(1), download data(2), convert to CSV(3), export(4).")

  val userConf = opt[File](noshort = true, descr = "The path of the user configuration file.")

  val inputMode = choice(choices= List("f", "c"), required = true, short = 'm', descr = "Where to get configuration from. Valid options are command line or config file")

  addValidation(validateFileInputMode)
  addValidation(validateCmdInputMode)
  conflicts(userConf, List(outputDir, geoSparkDistance, mergeWinds, mergeTemp, exportFormat, temperatureScale, numericType, activeSources, startAt))
  validateFileExists(userConf)
  verify()

  def getInputToMap() : Map[String, Any] = {
    val userInputMap = args.sliding(2, 2).map(a => (a.head.last, a.last)).toMap
    val optionsMap = builder.opts.filter{
      p => p.shortNames.nonEmpty && userInputMap.contains(p.shortNames(0))
    }.map{
      o => (o.shortNames(0), o.name)
    }.toMap

    optionsMap.join(userInputMap).map{case (_,v : Seq[String]) => (v.head, v.last)}
  }

  def validateFileInputMode : Either[String, Unit] = {
    if(inputMode.getOrElse("test") == "f" && userConf.isEmpty)
      Left("User configuration file (--user-conf) is mandatory in file mode")
    else
      Right(())
  }

  def validateCmdInputMode : Either[String, Unit] = {
    if(inputMode.getOrElse("") == "c" && outputDir.isEmpty)
      Left("Output directory (--output-dir) is mandatory in command line mode")
    else
      Right(())
  }
}