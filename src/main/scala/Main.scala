package main

import cats.effect._
import com.comcast.ip4s._
import controllers._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import repositories._
import services._

object Main extends IOApp {

  // Resource-safe way to initialize the transactor
  def transactorResource[F[_] : Async]: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool(32) // Specify thread pool size (32 threads in this case)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5450/bookingdb",
        user = "cashew_user",
        pass = "cashew",
        connectEC = ce // Connect execution context
      )
    } yield xa

  // Entry point
  override def run(args: List[String]): IO[ExitCode] = {
    // Resource composition
    transactorResource[IO].use { transactor =>
      val bookingRepository = new BookingRepository[IO](transactor)
      val bookingService = new BookingServiceImpl[IO](bookingRepository)

      // Define the HTTP app with the routes
      val httpApp = DeskController.deskRoutes(bookingService).orNotFound

      val server = EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)

      server
    }
  }
}
