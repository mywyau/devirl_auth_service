package controllers.test_routes

import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.*
import cats.implicits.*
import cats.Applicative
import configuration.AppConfig
import configuration.BaseAppConfig
import controllers.mocks.*
import controllers.test_routes.AuthRoutes.*
import controllers.BaseController
import dev.profunktor.redis4cats.RedisCommands
import doobie.util.transactor.Transactor
import fs2.kafka.*
import infrastructure.*
import infrastructure.KafkaProducerProvider
import java.net.URI
import java.time.Duration
import java.time.Instant
import models.auth.UserSession
import models.cache.CacheErrors
import models.cache.CacheSuccess
import models.cache.CacheUpdateSuccess
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.http4s.Uri
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.*
import services.*

object TestRoutes extends BaseAppConfig {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def baseRoutes(): HttpRoutes[IO] = {
    val baseController = BaseController[IO]()
    baseController.routes
  }

  def createTestRouter(appConfig: AppConfig, transactor: Transactor[IO]): Resource[IO, HttpRoutes[IO]] =
    for {
      authRoutes <- authRoutes(appConfig, transactor)
      registrationRoutes <- registrationRoutes(appConfig, transactor)
    } yield Router(
      "/devirl-auth-service" -> (
        baseRoutes() <+>
          authRoutes <+>
          registrationRoutes
      )
    )
}
