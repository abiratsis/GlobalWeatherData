package com.abiratsis.gweather

import com.abiratsis.gweather.config.Config
import com.abiratsis.gweather.shell.commands.{DownloadCommand, NcToCsvCommand, ShellCommand}
import com.abiratsis.gweather.common.{DataSourceContext, Util, implicits}
import com.abiratsis.gweather.spark.TemperatureDataset
import org.apache.spark.sql.SparkSession
import org.datasyslab.geosparksql.utils.GeoSparkSQLRegistrator

object Main extends App {
    implicit val conf = Config.current

    implicit val spark = SparkSession
        .builder()
        .appName("test")
        .master("local[*]")
        .config("spark.executor.memory", "6g")
        .config("spark.driver.memory", "1g")
        .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .getOrCreate()

    GeoSparkSQLRegistrator.registerAll(spark)

    conf match {
      case Left(ex) => println(ex)
      case Right(c) => {
        implicit val ds = DataSourceContext(c)
        val shell = ShellCommand

        val mergedDirsParams = ShellCommand.getParams(
          ds.downloadDirs,
          shell.dirCommandLineParams)

        val mergedSourcesParams = ShellCommand.getParams(
          ds.downloadSources,
          shell.sourcesCommandLineParams)

        val downloadCmd = new DownloadCommand
        //println(downloadCmd.execute(mergedDirsParams ++ mergedSourcesParams))

        val ncToCsvParams = NcToCsvCommand.getParams(
          ds.downloadSources,
          ds.downloadDirs,
          ds.sourcesByDir,
          shell.sourcesCommandLineParams)

        val ncToCsvCmd : NcToCsvCommand = new NcToCsvCommand

//        println(ncToCsvParams)
        //println(ncToCsvCmd.execute(ncToCsvParams))

        val tds = new TemperatureDataset

        println(tds.load.show(100))
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
