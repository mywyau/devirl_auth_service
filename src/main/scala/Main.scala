package main

import cats.NonEmptyParallel
import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import middleware.Middleware.throttleMiddleware
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import routes.Routes.*
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.effect.Log.Stdout._


object Main extends IOApp {

  // Logger for logging (optional but recommended for error handling)
  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  // Resource-safe way to initialize the transactor
  def transactorResource[F[_] : Async]: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool(32)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5450/cashew_db", // Moved to config/env variables later
        user = sys.env.getOrElse("DB_USER", "cashew_user"), // Default to "postgres"
        pass = sys.env.getOrElse("DB_PASS", "cashew"), // Default password
        connectEC = ce // Connect execution context (for managing connection pool)
      )
    } yield xa

  def createRouterResource[F[_] : Concurrent : Temporal : NonEmptyParallel : Async: Log](transactor: HikariTransactor[F]): Resource[F, HttpRoutes[F]] = {
    for {
      authRoutes <- createAuthRoutes(transactor) // Already returns Resource[F, HttpRoutes[F]]
      bookingRoutes <- Resource.pure(createBookingRoutes(transactor))
      businessRoutes <- Resource.pure(createBusinessRoutes(transactor))
      workspaceRoutes <- Resource.pure(createWorkspaceRoutes(transactor))

      // Combine all routes under the `/cashew` prefix
      combinedRoutes =
        Router(
          "/cashew" -> (authRoutes <+> bookingRoutes <+> businessRoutes <+> workspaceRoutes)
        )
      // Apply throttle middleware in the F effect context
      throttledRoutes <- Resource.eval(throttleMiddleware(combinedRoutes))
    } yield throttledRoutes
  }

  // Method to create the Ember server resource
  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Unit] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0") // Bind to all interfaces
      .withPort(port"8080") // Run on port 8080
      .withHttpApp(router.orNotFound) // Serve the HTTP app
      .build
      .void // Return `Unit` resource, the server runs and is cleaned up with resource management

  // Main program entry point
  override def run(args: List[String]): IO[ExitCode] = {
    transactorResource[IO].flatMap { transactor =>
      createRouterResource[IO](transactor).flatMap { router =>
        createServer(router)
      }
    }.use(_ => IO.never).as(ExitCode.Success)
  }
}
