package com.abiratsis.gweather.exceptions

class NullContextException(private val message : String = "Context is null. Please initialize context first.")
  extends NullPointerException(message)
