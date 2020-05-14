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

//Compile / packageBin / mappings += {
//  (baseDirectory.value / "scripts" / "download_weather.sh") -> "scripts/download_weather.sh"
//}

lazy val postBuild  = taskKey[Unit]("post build")
postBuild := {
  val log = streams.value.log
  val source = (baseDirectory.value / "scripts/download_weather.sh")
  val target = crossTarget.value / "scripts/download_weather.sh"

  log.info(s"Copying ${source.getPath} to ${target.getPath}")
  IO.copyFile(source, target)
  None
}

Compile / packageBin := (Compile / packageBin dependsOn postBuild).value

exportJars := true
logBuffered in Test := false