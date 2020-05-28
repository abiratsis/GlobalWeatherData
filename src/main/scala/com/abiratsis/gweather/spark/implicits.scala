package com.abiratsis.gweather.spark

import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.functions.col

object implicits {
  implicit class DataframeExt(val df: DataFrame) {
    def drop(cols: Seq[Column]) : DataFrame = {
      cols.foldLeft(df){
        (tdf, c) => tdf.drop(c)
      }
    }
  }

  implicit class SeqExt(val cols: Seq[String]) {
    def toCol(dfs: DataFrame*) : Seq[Column] = {
      if(dfs.nonEmpty) {
        dfs.foldLeft(Seq[Column]()) {
          (acc, df) => acc ++ cols.map {df(_)}
        }
      }
      else{
        cols.map {col(_)}
      }
    }
  }
}
