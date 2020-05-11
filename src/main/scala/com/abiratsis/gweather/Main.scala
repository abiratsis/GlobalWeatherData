package com.abiratsis.gweather

import com.abiratsis.gweather.config.Config
import com.abiratsis.gweather.shell.DownloadCommand
import com.abiratsis.gweather.utils.Util
//import org.apache.spark.sql.SparkSession


object Main extends App {
    val conf = Config.current

    conf match {
      case Left(ex) => println(ex)
      case Right(c) => {
        val downloadDirs = Util.ccToMap(c.dataSources.directories)
        val downloadSources = Util.ccToMap(c.dataSources.sources)

        val mergedDirsParams = DownloadCommand.getParams(downloadDirs, DownloadCommand.dirCommandLineParams)
        val mergedSourcesParams = DownloadCommand.getParams(downloadSources, DownloadCommand.sourcesCommandLineParams)

//        println(mergedDirsParams ++ mergedSourcesParams)
        val downloadCmd : DownloadCommand = new DownloadCommand
        println(downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams))
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
