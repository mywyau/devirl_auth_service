//
//ThisBuild / version := "0.1.0-SNAPSHOT"
//ThisBuild / scalaVersion := "3.3.0"
//
//lazy val root = (project in file("."))
//  .settings(
//    name := "cashew",
//    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
//    Compile / run / fork := true
//  )
//
//lazy val it = (project in file("it"))
//  .dependsOn(root)
//  .settings(
//    name := "cashew-it",
//    libraryDependencies ++= AppDependencies.integrationTest,
//    fork := true,
//    parallelExecution := true,
//    scalaSource := baseDirectory.value / "src" / "test" / "scala",
//  )


ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.0"

lazy val shared = (project in file("shared"))
  .settings(
      name := "cashew-shared",
      scalaVersion := scalaVersion.value,
      libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test ++ AppDependencies.integrationTest,

  )

lazy val root = (project in file("."))
  .dependsOn(shared) // Depend on shared module
  .settings(
      name := "cashew",
      libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
      Compile / run / fork := true
  )

lazy val it = (project in file("it"))
  .dependsOn(root, shared) // Depend on root and shared module
  .settings(
      name := "cashew-it",
      libraryDependencies ++= AppDependencies.integrationTest,
      fork := true,
      parallelExecution := true,
      scalaSource := baseDirectory.value / "src" / "test" / "scala",
      unmanagedSourceDirectories in Test += baseDirectory.value / "src" / "test" / "scala"
  )
