ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.0"

val http4sVersion = "0.23.28"

lazy val root = (project in file("."))
  .settings(
    name := "cashew",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.10.0",
      "org.typelevel" %% "cats-effect" % "3.5.1",
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-jawn" % http4sVersion,
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC4",
      "org.passay" % "passay" % "1.6.1",

      "io.circe" %% "circe-generic" % "0.14.7",
      "io.circe" %% "circe-core" % "0.14.7",
      "io.circe" %% "circe-parser" % "0.14.7",

      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC4" % Test,
      "com.disneystreaming" %% "weaver-cats" % "0.8.3" % Test
    ),
    Compile / run / fork := true
  )

// Define the integration test subproject as a separate project in the `it` directory
lazy val it = (project in file("it"))
  .dependsOn(root) // Depends on `root` for access to the main project’s code
  .settings(
    name := "cashew-it",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-h2" % "1.0.0-RC4",           // H2 for in-memory integration testing
      "org.flywaydb" % "flyway-core" % "8.5.0",              // Flyway for database migrations (if needed)
      "com.disneystreaming" %% "weaver-cats" % "0.8.3"       // Weaver for testing
    ),

    fork := true,
    parallelExecution := false,

    // Specify a flat directory structure for the integration test project
    scalaSource := baseDirectory.value / "src" / "test" / "scala"
  )
