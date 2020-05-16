package com.abiratsis.gweather.shell.commands

import com.abiratsis.gweather.utils.{Util, implicits}

class NcToCsvCommand extends ShellCommand {
  override protected def functionName: String = "nc_to_csv"
}

object NcToCsvCommand {

  def getParams(sources : Map[String, Any],
                dirs : Map[String, Any],
                sourcesCommandLineParams : Map[String, String]) : Seq[String] = {

    val sourcesPerDir = Map(
      "airTemperatureUrl" -> "temperatureDir",
      "skinTemperatureUrl" -> "temperatureDir",
      "maxTemperatureUrl" -> "temperatureDir",
      "minTemperatureUrl" -> "temperatureDir",
      "humidityUrl" -> "humidityDir",
      "uwindUrl" -> "windDir",
      "vwindUrl" -> "windDir",
      "clearSkyDownwardLongWaveUrl" -> "solarRadiationDir",
      "clearSkyDownwardSolarUrl" -> "solarRadiationDir",
      "downwardLongwaveRadiationUrl" -> "solarRadiationDir",
      "downwardSolarRadiationUrl" -> "solarRadiationDir",
      "netLongwaveRadiationUrl" -> "solarRadiationDir",
      "netShortwaveRadiationUrl" -> "solarRadiationDir"
    )

    import implicits._

    sources
      .join(sourcesPerDir).map{
        case  (k : String, v : Seq[Any]) =>
          (k, dirs(v(1).toString).toString + "/" + Util.getFileNameFromUrl(v(0).toString))
      }
      .join(sourcesCommandLineParams)
      .mapValues(p => s"""${p(1)} "${p(0)}"""")
      .values
      .toList
  }
}