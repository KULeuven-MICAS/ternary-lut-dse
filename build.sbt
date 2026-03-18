// See README.md for license details.

ThisBuild / scalaVersion := "2.13.14"
ThisBuild / version      := "0.1.0"
ThisBuild / organization := "MICAS (KU Leuven)"

Test / parallelExecution := true
Test / fork              := true

val chiselVersion = "6.4.0"
lazy val fpUnits  = ProjectRef(file("chisel-float"), "chisel_float")

lazy val root = (project in file("."))
  .settings(
    name := "ternary-lut",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel"     % chiselVersion,
      "org.scalatest"     %% "scalatest"  % "3.2.19" % "test",
      "edu.berkeley.cs"   %% "chiseltest" % "6.0.0"  % "test"
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations",
      "-Wunused" // Enable unused import fixes
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full)
  )
  .dependsOn(fpUnits, fpUnits % "compile->test")
