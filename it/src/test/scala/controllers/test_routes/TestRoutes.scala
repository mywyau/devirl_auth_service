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
// import controllers.test_routes.QuestRoutes.questRoutes
// import controllers.test_routes.UploadRoutes.*
import controllers.BaseController
// import controllers.PricingPlanController
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

  def createTestRouter(transactor: Transactor[IO], appConfig: AppConfig): Resource[IO, HttpRoutes[IO]] = {

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
      // consumerStream <- QuestCreatedConsumer.resource[IO](QuestCreatedConsumer.Settings(bootstrapServers = appConfig.kafka.bootstrapServers))
      // _ <- Resource
      //   .make(Concurrent[IO].start(consumerStream.compile.drain))(_.cancel)
      //   .void

      // questEventProducer = new QuestEventProducerImpl[IO](appConfig.kafka.topic.questCreated, kafkaProducer)
      // questEventProducer = new MockQuestEventProducer[IO]()
      // registrationRoutes <- registrationRoutes(transactor, appConfig)
      // userDataRoutes <- userDataRoutes(transactor, appConfig)
      // questRoute <- questRoutes(transactor, appConfig, questEventProducer)
      // uploadRoutes <- uploadRoutes(transactor, appConfig)
      // pricingPlanRoutes <- pricingPlanRoutes(transactor, appConfig, redisHost, redisPort)
    } yield Router(
      "/dev-quest-service" -> (
        baseRoutes() <+>
          authRoutes(redisHost, redisPort, transactor, appConfig) 
      )
    )
  }
}
