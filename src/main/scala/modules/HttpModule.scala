package modules

import cats.effect.*
import cats.syntax.all.*
import cats.Parallel
import com.comcast.ip4s.*
import configuration.AppConfig
import dev.profunktor.redis4cats.RedisCommands
import doobie.hikari.HikariTransactor
import middleware.Middleware
import org.http4s.client.Client
import org.http4s.headers.Origin
import org.http4s.server.middleware.CORS
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes, Method, Uri}
import org.typelevel.ci.CIString
import org.typelevel.log4cats.Logger
import routes.*
import scala.concurrent.duration.*

object HttpModule {

  def corsPolicy[F[_]: Async: Parallel: Logger](routes: HttpRoutes[F]): HttpRoutes[F] =
    CORS.policy
      .withAllowOriginHost(
        Set(
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(3000)),
          Origin.Host(Uri.Scheme.https, Uri.RegName("devirl.com"), None)
        )
      )
      .withAllowCredentials(true)
      .withAllowHeadersIn(
        Set(
          CIString("Content-Type"),
          CIString("Authorization"),
          CIString("X-Requested-With"),
          CIString("Accept"),
          CIString("Origin"),
          CIString("Referer"),
          CIString("Access-Control-Request-Method"),
          CIString("Access-Control-Request-Headers")
        )
      )
      .withMaxAge(1.day)
      .withAllowMethodsIn(Set(Method.GET, Method.POST, Method.PUT, Method.DELETE, Method.OPTIONS))
      .apply(routes)

  private def allRoutes[F[_]: Async: Parallel: Logger](
    appConfig: AppConfig,
    transactor: HikariTransactor[F],
    redisHost: String,
    redisPort: Int,
    redis: RedisCommands[F, String, String],
    httpClient: Client[F],
  ): HttpRoutes[F] =
    Router(
      "/dev-quest-service" -> (
        Routes.baseRoutes() <+>
        AuthRoutes.authRoutes(redisHost, redisPort, transactor, appConfig)
      )
    )

  def make[F[_]: Async: Parallel: Logger](
    appConfig: AppConfig,
    transactor: HikariTransactor[F],
    redis: RedisCommands[F, String, String],
    httpClient: Client[F],
  ): Resource[F, HttpApp[F]] = {

    val redisHost = appConfig.redisConfig.host
    val redisPort = appConfig.redisConfig.port

    val rawRoutes = allRoutes(appConfig, transactor, redisHost, redisPort, redis, httpClient)

    val withCors =
      if (appConfig.featureSwitches.useCors) corsPolicy(rawRoutes)
      else rawRoutes

    for {
      withMiddleware <- Resource.eval(Middleware.throttleMiddleware(withCors))
    } yield withMiddleware.orNotFound
  }
}
