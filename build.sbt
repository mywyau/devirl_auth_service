
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "cashew",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Compile / run / fork := true
  )

lazy val it = (project in file("it"))
  .dependsOn(root)
  .settings(
    name := "cashew-it",
    libraryDependencies ++= AppDependencies.integrationTest,
    fork := true,
    parallelExecution := false,
    scalaSource := baseDirectory.value / "src" / "test" / "scala"
  )
