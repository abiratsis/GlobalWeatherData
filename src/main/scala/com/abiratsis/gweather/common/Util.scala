package com.abiratsis.gweather.common

import java.nio.file.Paths
import scala.collection.immutable

object Util {
  /**
   * Converts case classes to map, taken from https://gist.github.com/lauris/7dc94fb29804449b1836#file-cctomap-scala
   *
   * @param cc The case class
   * @return A map that contains the
   */
  def ccToMap(cc: AnyRef) =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }

  /**
   * Extracts the file name from the given url.
   *
   * @param url
   * @return The file name
   */
  def getFileNameFromUrl(url : String) : String  = {
    Paths.get(url).getFileName.toString
  }

}

object implicits {
  implicit class MapExt[A, B, C](val left: immutable.Map[A, B]) {
    def join(right: immutable.Map[A, C]) : immutable.Map[A, Seq[_]] = {
      (left.toSeq ++ right.toSeq).groupBy(_._1).mapValues(_.map{_._2})
    }
  }
}
