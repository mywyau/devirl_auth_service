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
import org.http4s.server.middleware.Throttle
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories._
import services._

import scala.concurrent.duration._

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
        pass = sys.env.getOrElse("DB_PASS", "cashew_password"), // Default password
        connectEC = ce // Connect execution context (for managing connection pool)
      )
    } yield xa

  // Throttle middleware to apply rate limiting
  def throttleMiddleware[F[_] : Temporal](routes: HttpRoutes[F]): F[HttpRoutes[F]] = {
    Throttle.httpRoutes(
      amount = 500, // Maximum number of requests
      per = 1.minute // Time period for requests allowed, refreshes tokens in the bucket to allow for 500 requests per minute
    )(routes) // Apply throttling to the routes, returns F[HttpRoutes[F]]
  }

  def createBusinessRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    // Repositories, services, and controllers setup as before
    val businessRepository = new BusinessRepository[F](transactor)
    val businessService = new BusinessServiceImpl[F](businessRepository)
    val businessController = new BusinessControllerImpl[F](businessService)

    businessController.routes
  }


  def createRouterResource[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): Resource[F, HttpRoutes[F]] = {
    Resource.eval {
      // Repositories, services, and controllers setup as before
      val bookingRepository = new BookingRepository[F](transactor)
      val bookingService = new BookingServiceImpl[F](bookingRepository)
      val bookingController = new BookingControllerImpl[F](bookingService)

      // Apply throttle middleware
      throttleMiddleware(
        Router(
          "/cashew" -> bookingController.routes,
          "/cashew" -> createBusinessRoutes(transactor),
        )
      )
    }
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
