package com.abiratsis.gweather.spark.weather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.spark.GeoMetadata

abstract class WeatherMetadata extends GeoMetadata{
  var geoWeatherCtx: GeoWeatherContext

  /**
   * The netCDF files of the current dataset.
   */
  val netCDFSources : Map[String, String]

  /**
   * The value field in the netCDF file.
   */
  val netCDFFields: Map[String, String]
}
