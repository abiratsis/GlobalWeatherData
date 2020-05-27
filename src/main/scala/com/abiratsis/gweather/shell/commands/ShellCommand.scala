package com.abiratsis.gweather.shell.commands

import com.abiratsis.gweather.shell.ShellProxy
import com.abiratsis.gweather.common.implicits._

trait ShellCommand {
  /**
   * The function name in the shell script.
   *
   * @return
   */
  protected def functionName: String

  /**
   * Creates and returns shell execution string.
   *
   * @param params Parameters of the shell command
   * @return The shell command to be executed
   */
  def getExecString(params: Seq[String]): String = s"""$functionName ${params.mkString(" ")}"""

  /**
   * Executes the shell command.
   *
   * @param params Parameters of the shell command
   * @return The command-line output
   */
  def execute(params: Seq[String]): String = {
    ShellProxy.exec(this.getExecString(params))
  }
}

object ShellCommand{

  val sourcesCommandLineParams : Map[String, String] = Map(
    "airTemperatureUrl" -> "--airtemp",
    "skinTemperatureUrl" ->  "--skintemp",
    "maxTemperatureUrl" -> "--maxtemp",
    "minTemperatureUrl" -> "--mintemp",
    "humidityUrl" -> "--humid",
    "uwindUrl" -> "--uwind",
    "vwindUrl" -> "--vwind",
    "clearSkyDownwardLongWaveUrl" -> "--csdlf",
    "clearSkyDownwardSolarUrl" -> "--csdsf",
    "downwardLongwaveRadiationUrl" -> "--dlwrf",
    "downwardSolarRadiationUrl" -> "--dswrf",
    "netLongwaveRadiationUrl" -> "--nlwrs",
    "netShortwaveRadiationUrl" -> "--nswrs",
    "worldCountriesUrl" -> "--world"
  )

  val dirCommandLineParams : Map[String, String] = Map(
    "temperatureDir" -> "-t",
    "humidityDir" -> "-h",
    "windDir" -> "-w",
    "solarRadiationDir" -> "-s",
    "worldDir" -> "-r"
  )

  /**
   * Merges the given maps and returns the string representation of their values in order to handle easier
   * the command line input.
   *
   * Example: m1 = Map(k1 -> v11, k2 -> v21)
   *          m2 = Map(k1 -> v21, k2 -> v22)
   *
   *          getParams(m1, m2) == ["v21 v11", "v22 v21"]
   *
   * @param configParams
   * @param cmdParams
   * @return String Seq that contains the values of the matching pairs.
   */
  def getParams(configParams : Map[String, Any], cmdParams : Map[String, Any]) : Seq[String] ={
      configParams.join(cmdParams)
      .mapValues(p => s"""${p.last} "${p.head}"""")
      .values
      .toList
  }
}