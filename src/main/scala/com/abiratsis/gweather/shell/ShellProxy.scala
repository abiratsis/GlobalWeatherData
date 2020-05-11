package com.abiratsis.gweather.shell

import sys.process._

object ShellProxy {
  private final val shell = "/bin/sh"
  private final val scriptFile = "/Users/abiratsis/Desktop/GlobalWeatherData/scripts/download_weather_data.sh &&"

  def exec(cmd: String): String = s". $scriptFile $cmd" !!
}
