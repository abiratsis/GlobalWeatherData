package com.abiratsis.gweather.common

import java.nio.file.Paths

import scala.collection.immutable
import scala.reflect.io.Directory
import java.io.File

object Util {
  /**
   * Converts case classes to map, taken from https://gist.github.com/lauris/7dc94fb29804449b1836#file-cctomap-scala
   *
   * @param cc The case class
   * @return The map that contains the class members
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

  def deleteDir(dir: String) = {
    val directory = new Directory(new File(dir))

    if(directory.exists) {
      println(s"$dir was found. Deleting it...")
      val res = directory.deleteRecursively()

      if(res)
        println(s"$dir deleted....")
      else
        println(s"$dir deletion failed...")
    }
    else{
      println(s"$dir not found!")
    }
  }

  def deleteFile(path: String) = new File(path).delete()

  def isNullOrEmpty[T](s: Iterable[T]) = s match {
    case null => true
    case _ => s.toSeq.isEmpty
  }
}

object String {
  def isNullOrEmpty(str: java.lang.String) : Boolean = {
    str == null || str.isEmpty
  }
}

object implicits {
  type A = Any
  implicit class MapExt[K, B <: A, C <: A](val left: immutable.Map[K, B]) {
    def join(right: immutable.Map[K, C]) : immutable.Map[K, Seq[A]] = {
      val inter = left.keySet.intersect(right.keySet)

      val leftFiltered =  left.filterKeys{inter.contains}
      val rightFiltered = right.filterKeys{inter.contains}

      (leftFiltered.toSeq ++ rightFiltered.toSeq)
        .groupBy(_._1)
        .mapValues(_.map{_._2}.toList)
    }
  }
}