package com.abiratsis.gweather.shell.commands

import com.abiratsis.gweather.common.{Util, implicits}

class NcToCsvCommand extends ShellCommand {
  override protected def functionName: String = "nc_to_csv"
}

object NcToCsvCommand {

  def getParams(sources : Map[String, Any],
                dirs : Map[String, Any],
                sourcesPerDir : Map[String, String],
                sourcesCommandLineParams : Map[String, String]) : Seq[String] = {

    import implicits._

    sources
      .join(sourcesPerDir).map{
        case  (k : String, v : Seq[Any]) =>
          (k, dirs(v(1).toString).toString + "/" + Util.getFileNameFromUrl(v(0).toString))
      }
      .join(sourcesCommandLineParams)
      .mapValues(p => s"""${p(1)} "${p(0)}"""")
      .values
      .toList
  }
}