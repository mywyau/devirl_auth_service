package routes

import infrastructure.cache.RedisCacheImpl
import infrastructure.cache.SessionCache
import infrastructure.cache.SessionCacheImpl
import cats.effect.*
import cats.NonEmptyParallel
import configuration.AppConfig
import controllers.*
import doobie.hikari.HikariTransactor
import java.net.URI
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
}
