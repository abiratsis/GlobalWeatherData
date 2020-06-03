package com.abiratsis.gweather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.shell.commands.{DownloadCommand, InstallPrerequisitesCommand, NcToCsvCommand, ShellCommand}
import com.abiratsis.gweather.spark.{WeatherAtLocationHandler, WorldDataset}
import com.abiratsis.gweather.spark.weather.WeatherDataset

class Pipeline(implicit ctx: GeoWeatherContext) {
  private val shell = ShellCommand

  private def installPrerequisites : Unit = {
    val installPrerequisitesCommand = new InstallPrerequisitesCommand()
    installPrerequisitesCommand.execute()
  }

  private def downloadData : Unit = {
    val mergedDirsParams = shell.getParams(ctx.downloadDirs, shell.dirCommandLineParams)
    val mergedSourcesParams = shell.getParams(ctx.activeDownloadSourceUrls, shell.sourcesCommandLineParams)

    val downloadCmd = new DownloadCommand()
    downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams :_*)
  }

  private def convertToCsv : Unit = {
    val ncToCsvParams = shell.getParams(ctx.activeLocalSources, shell.sourcesCommandLineParams)

    val ncToCsvCmd = new NcToCsvCommand()
    ncToCsvCmd.execute(ncToCsvParams:_*)
  }

  private def exportGeoWeatherData(format: String) : Unit = {
    WeatherDataset.mergeAndCreateWeatherTable()
    WorldDataset().createWorldTable()
    val finalDf = new WeatherAtLocationHandler()
    finalDf.save(ctx.conf.global.rootDir, format)
  }

  import ExecStep._
  def execute(startAt: ExecutionStep = instPre, format: String) = {

    if(startAt.id == 1)
      this.installPrerequisites

    if (startAt.id <= 2)
      this.downloadData

    if(startAt.id <= 3)
      this.convertToCsv

    this.exportGeoWeatherData(format)
  }
}

object ExecStep extends Enumeration {
  type ExecutionStep = Value
  val instPre = Value(1)
  val downData = Value(2)
  val toCsv = Value(3)
  val expData = Value(4)
}