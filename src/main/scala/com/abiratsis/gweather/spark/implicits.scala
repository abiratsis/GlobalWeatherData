package com.abiratsis.gweather.spark

import org.apache.spark.sql.{Column, DataFrame}

object implicits {
  implicit class DataframeExt(val df: DataFrame) {
    def drop(cols: Seq[Column]) : DataFrame = {
      cols.foldLeft(df){
        (tdf, c) => tdf.drop(c)
      }
    }
  }
}
