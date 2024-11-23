package routes

import cats.NonEmptyParallel
import cats.effect.*
import controllers.*
import controllers.desk_listing.DeskListingControllerImpl
import dev.profunktor.redis4cats.effect.Log
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import repositories.*
import repositories.business.DeskListingRepositoryImpl
import services.*
import services.business.desk_listing.DeskListingServiceImpl

object Routes {

  def deskListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F])(implicit logger: Logger[F]): HttpRoutes[F] = {

    val deskListingRepo = new DeskListingRepositoryImpl[F](transactor)

    val deskListingService = new DeskListingServiceImpl[F](deskListingRepo)
    val deskListingController = new DeskListingControllerImpl[F](deskListingService)

    deskListingController.routes
  }
}
