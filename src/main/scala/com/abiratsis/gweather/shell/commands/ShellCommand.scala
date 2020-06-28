package com.abiratsis.gweather.shell.commands

import com.abiratsis.gweather.common.Util
import com.abiratsis.gweather.shell.ShellProxy
import com.abiratsis.gweather.common.implicits._
import com.abiratsis.gweather.exceptions.NullOrEmptyArgumentException

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
  def getExecString(params: Seq[String]): String = Util.isNullOrEmpty(params) match {
    case true => s"$functionName"
    case _ => s"$functionName ${params.mkString(" ")}"
  }

  /**
   * Executes the shell command.
   *
   * @param params Parameters of the shell command
   * @return The command-line output
   */
  def execute(params: String*): String = {
    ShellProxy.exec(this.getExecString(params))
  }
}

object ShellCommand{

  val sourcesCommandLineParams : Map[String, String] = Map(
    "airTemperature" -> "--airtemp",
    "skinTemperature" ->  "--skintemp",
    "maxTemperature" -> "--maxtemp",
    "minTemperature" -> "--mintemp",
    "humidity" -> "--humid",
    "uwind" -> "--uwind",
    "vwind" -> "--vwind",
    "clearSkyDownwardLongWave" -> "--csdlf",
    "clearSkyDownwardSolar" -> "--csdsf",
    "downwardLongwaveRadiation" -> "--dlwrf",
    "downwardSolarRadiation" -> "--dswrf",
    "netLongwaveRadiation" -> "--nlwrs",
    "netShortwaveRadiation" -> "--nswrs",
    "worldCountries" -> "--world"
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
  def getParams(configParams : Map[String, Any], cmdParams : Map[String, Any]) : Seq[String] = {
    if (Util.isNullOrEmpty(configParams))
      throw new NullOrEmptyArgumentException("configParams")

    if (Util.isNullOrEmpty(cmdParams))
      throw new NullOrEmptyArgumentException("cmdParams")

    configParams.join(cmdParams)
      .mapValues(p => s"""${p.last} "${p.head}"""")
      .values
      .toList
  }
}