package com.abiratsis.gweather

import com.abiratsis.gweather
import com.abiratsis.gweather.common.GeoWeatherContext

class Pipeline private (val ctx: GeoWeatherContext) {
  import com.abiratsis.gweather.shell.commands.{DownloadCommand, InstallPrerequisitesCommand, NcToCsvCommand, ShellCommand}

  private val shell = ShellCommand

  private def installPrerequisites() : Unit = {
    val installPrerequisitesCommand = new InstallPrerequisitesCommand()
    installPrerequisitesCommand.execute()
  }

  private def downloadData() : Unit = {
      val mergedDirsParams = shell.getParams(ctx.downloadDirs, shell.dirCommandLineParams)
      val mergedSourcesParams = shell.getParams(ctx.activeDownloadSourceUrls, shell.sourcesCommandLineParams)

      val downloadCmd = new DownloadCommand()
      downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams: _*)
  }

  private def convertToCsv() : Unit = {
    val ncToCsvParams = shell.getParams(ctx.activeLocalSources, shell.sourcesCommandLineParams)
    val ncToCsvCmd = new NcToCsvCommand()

    ncToCsvCmd.execute(ncToCsvParams: _*)
  }

  private def exportGeoWeatherData() : Unit = {
    import com.abiratsis.gweather.spark.weather.WeatherDataset
    import com.abiratsis.gweather.spark.{WeatherAtLocationHandler, WorldDataset}

    WeatherDataset.mergeAndCreateWeatherTable()(ctx)
    WorldDataset()(ctx).createWorldTable()

    val weatherAtLocationHandler = new WeatherAtLocationHandler()(ctx)
    weatherAtLocationHandler.save(ctx.userConfig.outputDir, ctx.userConfig.exportFormat)
  }

  def execute(): Unit = {
      val startAt = ExecutionStep(ctx.userConfig.startAt)

      if (startAt.id == 1)
        this.installPrerequisites()
      if (startAt.id <= 2)
        this.downloadData()

      if (startAt.id <= 3)
        this.convertToCsv()

      this.exportGeoWeatherData()
  }
}

object Pipeline {
  import com.abiratsis.gweather.exceptions.NullContextException

  def apply()(implicit ctx: Option[GeoWeatherContext]): Pipeline = ctx match {
    case Some(ctx) => new Pipeline(ctx)
    case None => throw new NullContextException
  }
}

object ExecutionStep extends Enumeration {
  type ExecutionStep = Value
  val instPre: gweather.ExecutionStep.Value = Value(1)
  val downData: gweather.ExecutionStep.Value = Value(2)
  val toCsv: gweather.ExecutionStep.Value = Value(3)
  val expData: gweather.ExecutionStep.Value = Value(4)
}