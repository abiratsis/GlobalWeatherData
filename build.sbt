name := "GlobalWeatherData"

version := "0.1.0"

scalaVersion := "2.11.11"
val sparkVersion = "2.4.4"

resolvers ++= Seq(
  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/",
  "maven" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  //Spark
  "org.apache.spark"     %% "spark-core" % sparkVersion,
  "org.apache.spark"     %% "spark-sql"  % sparkVersion,
  "org.apache.spark"     %% "spark-hive" % sparkVersion,

  //Testing
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalactic" %% "scalactic" % "3.1.1",

  //Configuration
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",

  //Delta-Lake
  "io.delta" %% "delta-core" % "0.5.0",

  //GeoSpark
  "org.datasyslab" % "geospark-sql_2.3" % "1.3.1",
  "org.datasyslab" % "geospark-viz_2.3" % "1.3.1",
  "org.datasyslab" % "geospark" % "1.3.1",

  //Command-line
  "org.rogach" %% "scallop" % "3.4.0"
)

assemblyJarName in assembly:= "gweather.jar"

// Deduplication error, check the link below for more information
// https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

lazy val postBuild  = taskKey[Unit]("post build")
postBuild := {
  val log = streams.value.log
  val shellSource = baseDirectory.value / "scripts/download_weather.sh"
  val shellTarget = crossTarget.value / "scripts/download_weather.sh"
  val pySource = baseDirectory.value / "scripts/nc_to_csv.py"
  val pyTarget = crossTarget.value / "scripts/nc_to_csv.py"
  val execScriptSource = baseDirectory.value / "scripts/gweather.sh"
  val execScriptTarget = crossTarget.value / "gweather.sh"

  log.info(s"Copying ${shellSource.getPath} to ${shellTarget.getPath}")
  IO.copyFile(shellSource, shellTarget)

  log.info(s"Copying ${pySource.getPath} to ${pyTarget.getPath}")
  IO.copyFile(pySource, pyTarget)

  log.info(s"Copying ${execScriptSource.getPath} to ${execScriptTarget.getPath}")
  IO.copyFile(execScriptSource, execScriptTarget)
  None
}

Compile / packageBin := (Compile / packageBin dependsOn postBuild).value

scalacOptions := Seq("-deprecation", "-feature")

exportJars := true
logBuffered in Test := false