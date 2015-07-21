import NativePackagerKeys._

packageArchetype.java_application

name := "VergeSQL"

herokuAppName in Compile := "vergepostgresdatabase"

version      := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

  val akkaV = "2.3.9"
  val sprayV = "1.3.3"

libraryDependencies ++= Seq(
  "org.squeryl" % "squeryl_2.11" % "0.9.6-RC3",
  "postgresql" %  "postgresql" % "8.4-701.jdbc4",
  "org.specs2" %%  "specs2-core"  % "2.3.11" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.apache.httpcomponents" % "httpclient" % "4.3.3",
  "org.apache.httpcomponents" % "httpclient-cache" % "4.3.3",
  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4",
  "io.spray" %% "spray-can" % sprayV,
  "io.spray" %% "spray-routing" % sprayV,
  "io.spray" %% "spray-testkit" % sprayV  % "test",
  "com.typesafe.akka" %% "akka-actor"    % akkaV,
  "com.typesafe.akka" %% "akka-testkit"  % akkaV  % "test",
  "org.specs2" %% "specs2-core"  % "2.3.11" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "org.jsoup" % "jsoup" % "1.7.2",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.6"
)

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

parallelExecution in Test := false