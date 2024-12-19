package controllers

import cats.effect.*
import doobie.*
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.ExecutionContexts
import org.http4s.ember.client.EmberClientBuilder
import shared.{HttpClientResource, TransactorResource}
import weaver.{GlobalResource, GlobalWrite}
import com.comcast.ip4s.{ipv4, port}
import TestRoutes.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}

object ControllerSharedResource extends GlobalResource {

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool(4)
      xa: HikariTransactor[IO] <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5432/shared_test_db", // Moved to config/env variables later
        user = sys.env.getOrElse("TEST_DB_USER", "shared_user"), // Default to "postgres"
        pass = sys.env.getOrElse("TEST_DB_PASS", "share"), // Default password
        connectEC = ce
      )
      client <- EmberClientBuilder.default[IO].build
      sharedSingleInstanceServer <- createServer(createTestRouter(xa)) // create shared server for tests
      _ <- global.putR(TransactorResource(xa)) // Store repository.TransactorResource in global context
      _ <- global.putR(HttpClientResource(client))
    } yield ()
  }

  private def printSchema(xa: Transactor[IO]): IO[Unit] = {
    val schemaQuery =
      sql"""
        SELECT column_name, data_type, is_nullable
        FROM information_schema.columns
        WHERE table_name = 'user_login_details'
      """.query[(String, String, String)]
        .to[List]
        .transact(xa)

    schemaQuery.flatMap { schema =>
      IO {
        println("Table Schema for 'user_login_details':")
        schema.foreach { case (name, typ, nullable) =>
          println(s"Column: $name, Type: $typ, Nullable: $nullable")
        }
      }
    }
  }
}