package com.abiratsis.gweather.spark

abstract class GeoMetadata {
  /**
   * The source's url which consists also its key.
   */
  val sourceKeys : Set[String]

  /**
   * The CSV sources.
   */
  val csvSources: Map[String, String]
}
