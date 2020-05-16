package com.abiratsis.gweather.utils

import scala.collection.immutable

object implicits {
  implicit class MapExt[A, B, C](val left: immutable.Map[A, B]) {
    def join(right: immutable.Map[A, C]) : immutable.Map[A, Seq[_]] = {
      (left.toSeq ++ right.toSeq).groupBy(_._1).mapValues(_.map{_._2})
    }
  }
}
