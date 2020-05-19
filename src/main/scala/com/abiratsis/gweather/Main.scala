package com.abiratsis.gweather

import java.net.{URI, URL}

import com.abiratsis.gweather.config.Config
import com.abiratsis.gweather.shell.commands.{DownloadCommand, NcToCsvCommand, ShellCommand}
import com.abiratsis.gweather.common.{DataSourceHelper, Util, implicits}
//import org.apache.spark.sql.SparkSession


object Main extends App {
    implicit val conf = Config.current

    conf match {
      case Left(ex) => println(ex)
      case Right(c) => {
        val ds = DataSourceHelper(c)
        val shell = ShellCommand

        val mergedDirsParams = ShellCommand.getParams(ds.downloadDirs, shell.dirCommandLineParams)

        val mergedSourcesParams = ShellCommand.getParams(ds.downloadSources, shell.sourcesCommandLineParams)

        val downloadCmd = new DownloadCommand
        println(downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams))

        val ncToCsvParams = NcToCsvCommand.getParams(
          ds.downloadSources,
          ds.downloadDirs,
          ds.sourcesByDir,
          shell.sourcesCommandLineParams)

        val ncToCsvCmd : NcToCsvCommand = new NcToCsvCommand

//        println(ncToCsvParams)
        println(ncToCsvCmd.execute(ncToCsvParams))
      }
    }

//    lazy val spark = SparkSession
//      .builder()
//      .appName("test")
//      .master("local[*]")
//      .config("spark.executor.memory", "6g")
//      .config("spark.driver.memory", "1g")
//      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//      .getOrCreate()
//
//    import spark.implicits._
//    import org.apache.spark.sql.functions._

}
