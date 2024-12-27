
import cats.NonEmptyParallel
import cats.effect.*
import cats.effect.syntax.all.*
import cats.implicits.*
import com.comcast.ip4s.*
import configuration.ConfigReader
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import fs2.concurrent.Topic
import middleware.Middleware.throttleMiddleware
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import org.http4s.server.websocket.WebSocketBuilder2
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import routes.Routes.*

import scala.concurrent.duration.DurationInt

object Main extends IOApp {

  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def transactorResource[F[_] : Async]: Resource[F, HikariTransactor[F]] = {
    val dbUrl = s"jdbc:postgresql://${sys.env.getOrElse("DB_HOST", "shared-postgres-container")}:${sys.env.getOrElse("DB_PORT", "5432")}/${sys.env.getOrElse("DB_NAME", "shared_db")}"
    for {
      ce <- ExecutionContexts.fixedThreadPool(32)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = dbUrl,
        user = sys.env.getOrElse("DB_USER", "shared_user"),
        pass = sys.env.getOrElse("DB_PASS", "share"),
        connectEC = ce
      )
    } yield xa
  }

  def createHttpRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async](
                                                                                 transactor: HikariTransactor[F]
                                                                               ): Resource[F, HttpRoutes[F]] = {
    for {
      deskListingRoutes <- Resource.pure(deskListingRoutes(transactor))
      officeAddressRoutes <- Resource.pure(officeAddressRoutes(transactor))
      officeContactDetailsRoutes <- Resource.pure(officeContactDetailsRoutes(transactor))
      officeSpecificationsRoutes <- Resource.pure(officeSpecificationsRoutes(transactor))
      officeListingRoutes <- Resource.pure(officeListingRoutes(transactor))
      businessAddressRoutes <- Resource.pure(businessAddressRoutes(transactor))
      businessContactDetailsRoutes <- Resource.pure(businessContactDetailsRoutes(transactor))
      businessSpecificationsRoutes <- Resource.pure(businessSpecificationsRoutes(transactor))
      businessListingRoutes <- Resource.pure(businessListingRoutes(transactor))

      combinedRoutes = Router(
        "/pistachio" -> (
          deskListingRoutes <+>
            officeAddressRoutes <+>
            officeContactDetailsRoutes <+>
            officeSpecificationsRoutes <+>
            officeListingRoutes <+>
            businessAddressRoutes <+>
            businessContactDetailsRoutes <+>
            businessSpecificationsRoutes <+>
            businessListingRoutes
          )
      )

      // Wrap combined routes with CORS middleware
      corsRoutes = CORS.policy
        .withAllowOriginAll
        .withAllowCredentials(false)
        .withAllowHeadersAll
        .withMaxAge(1.day)
        .apply(combinedRoutes)

      // Apply throttle middleware
      throttledRoutes <- Resource.eval(throttleMiddleware(corsRoutes))
    } yield throttledRoutes
  }

  def createServer[F[_] : Async](
                                  host: Host,
                                  port: Port,
                                  httpRoutes: HttpRoutes[F]
                                ): Resource[F, Unit] = {
    EmberServerBuilder
      .default[F]
      .withHost(host)
      .withPort(port)
      .withHttpApp(httpRoutes.orNotFound)
      .build
      .void
  }

  override def run(args: List[String]): IO[ExitCode] = {

    val configReader = ConfigReader[IO]

    for {
      appConfig <- configReader.loadAppConfig.handleErrorWith { e =>
        IO.raiseError(new RuntimeException(s"Failed to load app configuration: ${e.getMessage}", e))
      }
      _ <- Logger[IO].info(s"Loaded configuration: $appConfig")
      host <- IO.fromOption(Host.fromString(appConfig.localConfig.serverConfig.host))(new RuntimeException("Invalid host in configuration"))
      port <- IO.fromOption(Port.fromInt(appConfig.localConfig.serverConfig.port))(new RuntimeException("Invalid port in configuration"))
      exitCode: ExitCode <-
        transactorResource[IO].flatMap { transactor =>

            val httpRoutesResource: Resource[IO, HttpRoutes[IO]] = createHttpRoutes[IO](transactor)

            httpRoutesResource.flatMap { httpRoutes =>
              createServer(
                host,
                port,
                httpRoutes
              )
            }
          }.use(_ => IO.never)
          .as(ExitCode.Success)
    } yield {
      exitCode
    }
  }
}
