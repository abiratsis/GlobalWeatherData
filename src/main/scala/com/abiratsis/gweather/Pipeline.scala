package com.abiratsis.gweather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.exceptions.NullContextException
import com.abiratsis.gweather.shell.commands.{DownloadCommand, InstallPrerequisitesCommand, NcToCsvCommand, ShellCommand}
import com.abiratsis.gweather.spark.{WeatherAtLocationHandler, WorldDataset}
import com.abiratsis.gweather.spark.weather.WeatherDataset

class Pipeline(implicit context: Option[GeoWeatherContext]) {
  private val shell = ShellCommand

  private def installPrerequisites : Unit = {
    val installPrerequisitesCommand = new InstallPrerequisitesCommand()
    installPrerequisitesCommand.execute()
  }

  private def downloadData : Unit = context match {
    case Some(ctx) => {
      val mergedDirsParams = shell.getParams(ctx.downloadDirs, shell.dirCommandLineParams)
      val mergedSourcesParams = shell.getParams(ctx.activeDownloadSourceUrls, shell.sourcesCommandLineParams)

      val downloadCmd = new DownloadCommand()
      downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams: _*)
    }
    case None => throw new NullContextException
  }

  private def convertToCsv : Unit = context match {
    case Some(ctx) => {
      val ncToCsvParams = shell.getParams(ctx.activeLocalSources, shell.sourcesCommandLineParams)

      val ncToCsvCmd = new NcToCsvCommand()
      ncToCsvCmd.execute(ncToCsvParams: _*)
    }
    case None => throw new NullContextException
  }

  private def exportGeoWeatherData() : Unit = context match {
    case Some(ctx) => {
      WeatherDataset.mergeAndCreateWeatherTable()
      WorldDataset()(ctx).createWorldTable()

      val weatherAtLocationHandler = new WeatherAtLocationHandler()(ctx)
      weatherAtLocationHandler.save(ctx.userConfig.outputDir, ctx.userConfig.exportFormat)
    }
    case None => throw new NullContextException
  }

  def execute = context match {
    case Some(ctx) => {
      val startAt = ExecutionStep(ctx.userConfig.startAt)

      if (startAt.id == 1)
        this.installPrerequisites
      if (startAt.id <= 2)
        this.downloadData

      if (startAt.id <= 3)
        this.convertToCsv

      this.exportGeoWeatherData
    }
    case None => throw new NullContextException
  }
}

object ExecutionStep extends Enumeration {
  type ExecutionStep = Value
  val instPre = Value(1)
  val downData = Value(2)
  val toCsv = Value(3)
  val expData = Value(4)
}