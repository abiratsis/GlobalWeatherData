package com.abiratsis.gweather.shell

trait ShellCommand {
  protected def functionName: String
  def execute(params: Seq[String]) : String
}

class DownloadCommand extends ShellCommand{
  protected override val functionName: String = "download_sources"
  override def execute(params: Seq[String]): String = {
    val executable = s"""$functionName ${params.mkString(" ")}"""

    ShellProxy.exec(executable)
  }
}

object DownloadCommand{
  val dirCommandLineParams : Map[String, String] = Map(
    "temperatureDir" -> "-t",
    "humidityDir" -> "-h",
    "windDir" -> "-w",
    "solarRadiationDir" -> "-s"
  )

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
    "netShortwaveRadiationUrl" -> "--nswrs"
  )

  def getParams(configParams : Map[String, Any], cmdParams : Map[String, Any]) : Seq[String] ={
    (configParams.toSeq ++ cmdParams.toSeq)
      .groupBy(_._1)
      .mapValues(p => s"""${p(1)._2} "${p(0)._2}"""")
      .values
      .toList
  }
}