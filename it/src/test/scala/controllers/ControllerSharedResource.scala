package controllers

import cats.effect.*
import dev.profunktor.redis4cats.effect.Log
import doobie.*
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.ExecutionContexts
import org.http4s.ember.client.EmberClientBuilder
import weaver.{GlobalResource, GlobalWrite}

object ControllerSharedResource extends GlobalResource {

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] = {
    for {
      // Create the database transactor
      ce <- ExecutionContexts.fixedThreadPool(4)
      xa <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5450/cashew_test_db",
        user = sys.env.getOrElse("TEST_DB_USER", "cashew_user"),
        pass = sys.env.getOrElse("TEST_DB_PASS", "cashew"),
        connectEC = ce
      )
      client <- EmberClientBuilder.default[IO].build
      // Store the TransactorResource and Client globally
      _ <- global.putR(TransactorResource(xa))
      _ <- global.putR(HttpClientResource(client))
      // Optionally, you can add printSchema or testInsert calls here if needed
    } yield ()
  }

  private def printSchema(xa: Transactor[IO]): IO[Unit] = {
    val schemaQuery =
      sql"""
        SELECT column_name, data_type, is_nullable
        FROM information_schema.columns
        WHERE table_name = 'user_profile'
      """.query[(String, String, String)]
        .to[List]
        .transact(xa)

    schemaQuery.flatMap { schema =>
      IO {
        println("Table Schema for 'user_profile':")
        schema.foreach { case (name, typ, nullable) =>
          println(s"Column: $name, Type: $typ, Nullable: $nullable")
        }
      }
    }
  }
}