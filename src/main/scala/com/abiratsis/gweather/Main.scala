package com.abiratsis.gweather

import com.abiratsis.gweather.config.Config
import org.apache.spark.sql.SparkSession



object Main {
  def main(args: Array[String]): Unit = {

    println (Config.dataSources.right.get)

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

}
