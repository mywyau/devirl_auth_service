package kafka

import cats.effect.*
import configuration.models.*
import configuration.BaseAppConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import fs2.kafka.Acks
import fs2.kafka.KafkaProducer
import fs2.kafka.ProducerSettings
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import scala.concurrent.ExecutionContext
import shared.KafkaProducerResource
import shared.TransactorResource
import weaver.GlobalResource
import weaver.GlobalWrite

object KafkaSharedResource extends GlobalResource with BaseAppConfig {

  def executionContextResource: Resource[IO, ExecutionContext] =
    ExecutionContexts.fixedThreadPool(4)

  def kafkaProducerResource(): Resource[IO, KafkaProducer[IO, String, String]] =
    KafkaProducer.resource(
      ProducerSettings[IO, String, String]
        .withBootstrapServers("localhost:9092")
        .withAcks(Acks.All)
    )

  def transactorResource(postgresqlConfig: PostgresqlConfig, ce: ExecutionContext): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = "org.postgresql.Driver",
      url = s"jdbc:postgresql://${postgresqlConfig.host}:${postgresqlConfig.port}/${postgresqlConfig.dbName}",
      user = postgresqlConfig.username,
      pass = postgresqlConfig.password,
      connectEC = ce
    )

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] =
    for {
      appConfig <- appConfigResource
      ce <- executionContextResource
      postgresqlConfig: PostgresqlConfig = appConfig.postgresqlConfig
      xa <- transactorResource(postgresqlConfig, ce)
      kafkaProducer <- kafkaProducerResource()
      _ <- global.putR(TransactorResource(xa))
      _ <- global.putR(KafkaProducerResource(kafkaProducer))
    } yield ()
}
