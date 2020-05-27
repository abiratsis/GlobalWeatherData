package com.abiratsis.gweather

import com.abiratsis.gweather.common.DataSourceContext
import org.apache.spark.sql.{DataFrame, SparkSession}

class Pipeline(implicit val dsCtx: DataSourceContext, implicit val spark: SparkSession) {

  def getMergedWeatherData() : DataFrame = {
    val tempDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("temperatureDir") + "/merged")
      .cache()

    val humDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("humidityDir") + "/merged")
      .cache()

    val windDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("windDir") + "/merged")
      .cache()

    val solarDf = spark.read
      .format("delta")
      .load(dsCtx.downloadDirs("solarRadiationDir") + "/merged")
      .cache()

    val tempCount = tempDf.count()
    assert(tempCount == windDf.count())
    assert(tempCount == humDf.count())
    //assert(tempCount == solarDf.count())

    println("temp count:" + tempCount)
    println("solar count:" + solarDf.count())

    val cols = Seq("date", "lon", "lat")
    tempDf.join(windDf, cols, "inner")
      .join(solarDf, cols, "inner")
      .join(humDf, cols, "inner")
      .cache()
  }

}
