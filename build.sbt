name := "GlobalWeatherData"

version := "0.1"

scalaVersion := "2.11.11"
val sparkVersion = "2.4.4"

resolvers ++= Seq(
  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/",
  "maven" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "org.apache.spark"     %% "spark-core" % sparkVersion,
  "org.apache.spark"     %% "spark-sql"  % sparkVersion,
  "org.apache.spark"     %% "spark-hive" % sparkVersion,

  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalactic" %% "scalactic" % "3.1.1",

  "com.github.pureconfig" %% "pureconfig" % "0.12.3",

  "io.delta" %% "delta-core" % "0.5.0",

  "org.datasyslab" % "geospark-sql_2.3" % "1.3.1",
  "org.datasyslab" % "geospark-viz_2.3" % "1.3.1",
  "org.datasyslab" % "geospark" % "1.3.1"
)

//Compile / packageBin / mappings += {
//  (baseDirectory.value / "scripts" / "download_weather.sh") -> "scripts/download_weather.sh"
//}

lazy val postBuild  = taskKey[Unit]("post build")
postBuild := {
  val log = streams.value.log
  val shellSource = (baseDirectory.value / "scripts/download_weather.sh")
  val shellTarget = crossTarget.value / "scripts/download_weather.sh"
  val pySource = (baseDirectory.value / "scripts/nc_to_csv.py")
  val pyTarget = crossTarget.value / "scripts/nc_to_csv.py"

  log.info(s"Copying ${shellSource.getPath} to ${shellTarget.getPath}")
  IO.copyFile(shellSource, shellTarget)

  log.info(s"Copying ${pySource.getPath} to ${pyTarget.getPath}")
  IO.copyFile(pySource, pyTarget)

  None
}

Compile / packageBin := (Compile / packageBin dependsOn postBuild).value

exportJars := true
logBuffered in Test := false