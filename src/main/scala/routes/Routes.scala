package routes

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

object Routes {

  def baseRoutes[F[_] : Concurrent : Logger](): HttpRoutes[F] = {

    val baseController = BaseController()

    baseController.routes
  }

  def authRoutes[F[_] : Async : Logger](
    appConfig: AppConfig,
    transactor: HikariTransactor[F]
  ): HttpRoutes[F] = {

    val userDataRepository = new UserDataRepositoryImpl(transactor)
    val sessionCache = SessionCache(appConfig)
    val sessionService = SessionService(userDataRepository, sessionCache)
    val authController = AuthController(sessionService)

    authController.routes
  }

  def registrationRoutes[F[_] : Async : NonEmptyParallel : Logger](
    appConfig: AppConfig,
    transactor: HikariTransactor[F],
    registrationEventProducer: RegistrationEventProducerAlgebra[F]
  ): HttpRoutes[F] = {

    val sessionCache = SessionCache(appConfig)
    val userDataRepository = new UserDataRepositoryImpl(transactor)
    val outboxRepositoryImpl = new OutboxRepositoryImpl(transactor)
    val registrationService = RegistrationService(userDataRepository, outboxRepositoryImpl, registrationEventProducer)
    val registrationController = RegistrationController(registrationService, sessionCache)

    registrationController.routes
  }

}
