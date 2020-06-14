package com.abiratsis.gweather.exceptions

class NullOrEmptyArgumentException(paramName: String)
  extends IllegalArgumentException(s"$paramName can't be null or empty.")