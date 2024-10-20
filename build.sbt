ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

val http4sVersion = "0.23.28"

lazy val root = (project in file("."))
  .settings(
    name := "cashew",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.10.0",
      "org.typelevel" %% "cats-effect" % "3.5.1", // Ensure you match the correct cats-effect version
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-jawn" % http4sVersion,
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC4",

      "io.circe" %% "circe-generic" % "0.14.7",
      "io.circe" %% "circe-core" % "0.14.7",
      "io.circe" %% "circe-parser" % "0.14.7",

      "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC4" % Test,
      "org.mockito" %% "mockito-scala" % "1.17.12" % Test,
      "org.flywaydb" % "flyway-core" % "8.5.0", // for easier db migrations and handling

      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC2" % Test, // Doobie integration with ScalaTest,
      "com.disneystreaming" %% "weaver-cats" % "0.8.3" % Test
    )
  )

Compile / run / fork := true