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
import infrastructure.cache.*
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

  def createTestRouter(appConfig: AppConfig, transactor: Transactor[IO]): Resource[IO, HttpRoutes[IO]] = {

    val redisHost = sys.env.getOrElse("REDIS_HOST", appConfig.redisConfig.host)
    val redisPort = sys.env.get("REDIS_PORT").flatMap(p => scala.util.Try(p.toInt).toOption).getOrElse(appConfig.redisConfig.port)

    for {
      kafkaProducer: KafkaProducer[IO, String, String] <- KafkaProducerProvider.make[IO](
        appConfig.kafka.bootstrapServers,
        appConfig.kafka.clientId,
        appConfig.kafka.acks,
        appConfig.kafka.lingerMs,
        appConfig.kafka.retries
      )
    } yield Router(
      "/dev-quest-service" -> (
        baseRoutes() <+>
          authRoutes(appConfig, transactor)
      )
    )
  }
}
