import cats.NonEmptyParallel
import cats.effect.*
import cats.effect.syntax.all.*
import cats.implicits.*
import com.comcast.ip4s.*
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.effect.Log.Stdout.*
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
    val dbUrl = s"jdbc:postgresql://${sys.env.getOrElse("DB_HOST", "postgres")}:${sys.env.getOrElse("DB_PORT", "5432")}/${sys.env.getOrElse("DB_NAME", "shared_db")}"
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

  def createHttpRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](
                                                                                       transactor: HikariTransactor[F]
                                                                                     ): Resource[F, HttpRoutes[F]] = {
    for {
      deskListingRoutes <- Resource.pure(deskListingRoutes(transactor))

      // Combine all routes under the `/cashew` prefix
      combinedRoutes = Router(
        "/cashew" -> (
          deskListingRoutes
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

  def createServer[F[_] : Async](httpRoutes: HttpRoutes[F]): Resource[F, Unit] = {
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(httpRoutes.orNotFound)
      .build
      .void
  }

  override def run(args: List[String]): IO[ExitCode] = {

    transactorResource[IO].flatMap { transactor =>
      val httpRoutesResource: Resource[IO, HttpRoutes[IO]] = createHttpRoutes[IO](transactor)

      httpRoutesResource.flatMap { httpRoutes =>
        createServer(httpRoutes)
      }
    }.use(_ => IO.never).as(ExitCode.Success)
  }

}
