import cats.NonEmptyParallel
import cats.effect._
import cats.implicits._
import com.comcast.ip4s._
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.effect.Log.Stdout._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import middleware.Middleware.throttleMiddleware
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import org.http4s.server.websocket.WebSocketBuilder2
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import routes.Routes._
import websockets.DeskBookingServiceImpl

import scala.concurrent.duration.DurationInt

object Main extends IOApp {

  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def transactorResource[F[_] : Async]: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool(32)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5450/cashew_db",
        user = sys.env.getOrElse("DB_USER", "cashew_user"),
        pass = sys.env.getOrElse("DB_PASS", "cashew"),
        connectEC = ce
      )
    } yield xa

  def createHttpRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](
                                                                                       transactor: HikariTransactor[F]
                                                                                     ): Resource[F, HttpRoutes[F]] = {
    for {
      authRoutes <- Resource.pure(createAuthRoutes(transactor))
      registrationRoutes <- Resource.pure(registrationRoutes(transactor))
      loginRoutes <- Resource.pure(loginRoutes(transactor))
      wandererAddressRoutes <- Resource.pure(wandererAddressRoutes(transactor))
      bookingRoutes <- Resource.pure(createBookingRoutes(transactor))
      businessRoutes <- Resource.pure(createBusinessRoutes(transactor))
      workspaceRoutes <- Resource.pure(createWorkspaceRoutes(transactor))

      // Combine all routes under the `/cashew` prefix
      combinedRoutes = Router(
        "/cashew" -> (
          authRoutes <+> registrationRoutes <+> loginRoutes <+> wandererAddressRoutes <+>
            bookingRoutes <+> businessRoutes <+> workspaceRoutes
          )
      )

      // Wrap combined routes with CORS middleware
      corsRoutes = CORS.policy
        .withAllowOriginAll
        .withAllowCredentials(false)
        .withMaxAge(1.day)
        .apply(combinedRoutes)

      // Apply throttle middleware
      throttledRoutes <- Resource.eval(throttleMiddleware(corsRoutes))
    } yield throttledRoutes
  }

  def createWebSocketRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log]: WebSocketBuilder2[F] => HttpRoutes[F] = {
    val deskBookingService = new DeskBookingServiceImpl[F]
    builder => deskBookingService.deskAvailabilityWebSocket(builder)
  }

  def createServer[F[_] : Async](
                                  httpRoutes: HttpRoutes[F],
                                  webSocketRoutes: WebSocketBuilder2[F] => HttpRoutes[F]
                                ): Resource[F, Unit] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpRoutes.orNotFound)
      .withHttpWebSocketApp(wsBuilder => webSocketRoutes(wsBuilder).orNotFound)
      .build
      .void

  override def run(args: List[String]): IO[ExitCode] = {
    transactorResource[IO].flatMap { transactor =>
      val httpRoutesResource = createHttpRoutes[IO](transactor)
      val webSocketRoutesResource = createWebSocketRoutes[IO]

      httpRoutesResource.flatMap { httpRoutes =>
        createServer(httpRoutes, webSocketRoutesResource)
      }
    }.use(_ => IO.never).as(ExitCode.Success)
  }
}
