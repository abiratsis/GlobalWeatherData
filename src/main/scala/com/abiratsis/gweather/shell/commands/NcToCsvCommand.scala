package com.abiratsis.gweather.shell.commands

import com.abiratsis.gweather.common.{Util, implicits}

/**
 * Handles the .nc to .csv conversion of the data.
 */
class NcToCsvCommand extends ShellCommand {
  override protected def functionName: String = "nc_to_csv"
}