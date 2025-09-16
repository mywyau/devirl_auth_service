package routes

import cats.effect.*
import cats.NonEmptyParallel
import configuration.AppConfig
import controllers.*
import doobie.hikari.HikariTransactor
import infrastructure.cache.RedisCacheImpl
import infrastructure.cache.SessionCache
import infrastructure.cache.SessionCacheImpl
import java.net.URI
import org.http4s.client.Client
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import repositories.*
import services.*

object AuthRoutes {

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
}
