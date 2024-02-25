ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "sudoku",
    idePackagePrefix := Some("junicamp"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "utest" % "0.8.2" % Test
    ),
    testFrameworks ++= Seq(
      new TestFramework("utest.runner.Framework")
    ),
  )
