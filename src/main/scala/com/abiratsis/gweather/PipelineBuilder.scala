package com.abiratsis.gweather

import com.abiratsis.gweather.common.GeoWeatherContext
import com.abiratsis.gweather.config.{ApplicationSettings, UserSettings}

class PipelineBuilder {
  private val settingsMap:scala.collection.mutable.Map[String, Any] = scala.collection.mutable.Map[String, Any] ()

  def withParameter(name: String, value: Any) : PipelineBuilder = {
    settingsMap(name) = value
    this
  }

  def build() : Pipeline = {
    val userSettings = UserSettings(settingsMap.toMap)
    val appSettings = ApplicationSettings()

    Pipeline()(Some(GeoWeatherContext(appSettings, userSettings)))
  }
}
