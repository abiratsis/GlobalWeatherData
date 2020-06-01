package com.abiratsis.gweather

import com.abiratsis.gweather.common.DataSourceContext
import com.abiratsis.gweather.config.Config
import com.abiratsis.gweather.shell.commands.{DownloadCommand, NcToCsvCommand, ShellCommand}
import com.abiratsis.gweather.spark.{WeatherAtLocationHandler, WorldDataset}
import com.abiratsis.gweather.spark.weather.{HumidityDataset, SolarDataset, WeatherDataset, WindDataset}

//import org.apache.log4j.{Level, Logger}

object Main extends App {
  //    Logger.getLogger("org")

  implicit val conf = Config.current

  conf match {
    case Left(ex) => println(ex)
    case Right(c) => {
      implicit val ds = DataSourceContext(c)
      val shell = ShellCommand

      val mergedDirsParams = ShellCommand.getParams(ds.downloadDirs, shell.dirCommandLineParams)

      val mergedSourcesParams = ShellCommand.getParams(ds.activeDownloadSourceUrls, shell.sourcesCommandLineParams)

      val downloadCmd = new DownloadCommand
//      downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams)

      val ncToCsvParams = ShellCommand.getParams(ds.activeLocalSources, shell.sourcesCommandLineParams)

//      println(ncToCsvParams)
      val ncToCsvCmd: NcToCsvCommand = new NcToCsvCommand
//      ncToCsvCmd.execute(ncToCsvParams)

//      val tds = new TemperatureDataset
//      tds.saveAsDelta()

//      val hds = HumidityDataset()
//      hds.saveAsDelta()

//      val wds = WindDataset()
//      wds.load()

//      val sdt = SolarDataset()
//      sdt.saveAsDelta()

//      val wrds = WorldDataset()
//      wrds.saveAsDelta()

//      val pipeline = new Pipeline()
//      pipeline.mergeAndCreateWeatherTable().show()
//      val weatherDf = WeatherDataset.mergeAndCreateWeatherTable(ds, spark)
//      weatherDf.show()

      val weatherDf = WeatherDataset.mergeAndCreateWeatherTable()
      val worldDf = WorldDataset().createWorldTable()
      val finalDf = new WeatherAtLocationHandler()
      finalDf.save("/Users/abiratsis/Desktop/", "delta")
    }
  }
}
