package com.abiratsis.gweather.shell

import sys.process._
import scala.language.postfixOps

object ShellProxy {
  private final val scriptFile = "scripts/download_weather.sh"

  def exec(cmd: String): String = {
     Seq("sh", "-c", s"source $scriptFile $cmd") !!
  }
}
