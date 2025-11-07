package background_jobs

import _root_.services.services.outbox.OutboxPublisherService
import cats.effect.*
import cats.NonEmptyParallel
import configuration.AppConfig
import controllers.*
import doobie.hikari.HikariTransactor
import infrastructure.*
import java.net.URI
import kafka.*
import org.http4s.client.Client
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import repositories.*
import services.*

object BackgroundJobs {

  def authRoutes[F[_] : Async : Logger](
    appConfig: AppConfig,
    registrationEventProducer: RegistrationEventProducerAlgebra[F],
    transactor: HikariTransactor[F]
  ): HttpRoutes[F] = {

    val userDataRepository = OutboxRepository(transactor)
    val outboxPublisherService = OutboxPublisherService(userDataRepository, registrationEventProducer, "", )

    authController.routes
  }

}
