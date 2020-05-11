name := "GlobalWeatherData"

version := "0.1"

scalaVersion := "2.11.11"
val sparkVersion = "2.4.4"

resolvers ++= Seq(
  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/",
  "maven" at "https://repo1.maven.org/maven2/"
)

libraryDependencies ++= Seq(
//  "org.apache.spark"     %% "spark-core" % sparkVersion,
//  "org.apache.spark"     %% "spark-sql"  % sparkVersion,
//  "org.apache.spark"     %% "spark-hive" % sparkVersion,

  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalactic" %% "scalactic" % "3.1.1",

  "com.github.pureconfig" %% "pureconfig" % "0.12.3"
)

logBuffered in Test := false