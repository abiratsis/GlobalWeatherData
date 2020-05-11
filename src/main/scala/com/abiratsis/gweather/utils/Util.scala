package com.abiratsis.gweather.utils

object Util {
  // https://gist.github.com/lauris/7dc94fb29804449b1836#file-cctomap-scala
  def ccToMap(cc: AnyRef) =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }


}
