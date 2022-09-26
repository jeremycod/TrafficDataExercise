import Dependencies._

name := "TrafficDataExercise"

version := "0.1"


lazy val root = (project in file("."))
  .settings(
    name := "TrafficDataExercise",
    scalacOptions ++= Seq("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    scalaVersion := "2.13.9",
    Compile / mainClass := Some("com.mzinnovations.topl.Main"),
    Global / onChangedBuildSource := ReloadOnSourceChanges,
    libraryDependencies ++= rootDependencies
  )
