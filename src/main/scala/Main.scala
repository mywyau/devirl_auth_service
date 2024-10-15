package main

import cats.effect._
import cats.implicits._
import com.comcast.ip4s._
import controllers._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories._
import services._ // Brings in orNotFound for HttpRoutes

object Main extends IOApp {

  // Logger for logging (optional but recommended for error handling)
  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  // Resource-safe way to initialize the transactor
  def transactorResource[F[_] : Async]: Resource[F, HikariTransactor[F]] =
    for {
      // Create a fixed size connection pool (32 threads in this case)
      ce <- ExecutionContexts.fixedThreadPool(32)
      // Initialize HikariTransactor for the PostgreSQL database
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5450/cashew_db", // Moved to config/env variables later
        user = sys.env.getOrElse("DB_USER", "cashew_user"), // Default to "postgres"
        pass = sys.env.getOrElse("DB_PASS", "cashew"), // Default password
        connectEC = ce // Connect execution context (for managing connection pool)
      )
    } yield xa

  // Method to create routes by injecting services and repositories
  def createRouter[F[_] : Concurrent](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    // Repositories
    val bookingRepository = new BookingRepository[F](transactor) // Create repository instance

    // Services
    val bookingService = new BookingServiceImpl[F](bookingRepository) // Create service instance

    // Controllers
    val bookingController = new BookingController[F](bookingService) // Create controller instance

    // Combine the routes (more controllers can be added here)
    Router(
      "/api" -> bookingController.routes // Prefix all booking routes with /api
    )
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

    // Combine the transactor and server in a resource-safe way
    transactorResource[IO].flatMap { transactor =>
      createServer(createRouter(transactor))
    }.use(_ => IO.never).as(ExitCode.Success) // Keeps the server running
  }
}
