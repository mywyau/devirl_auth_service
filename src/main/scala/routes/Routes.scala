package routes

import cats.NonEmptyParallel
import cats.effect.*
import controllers.*
import controllers.desk_listing.DeskListingControllerImpl
import controllers.office_listing.OfficeListingControllerImpl
import controllers.business_listing.BusinessListingControllerImpl
import dev.profunktor.redis4cats.effect.Log
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import repositories.*
import repositories.business.{BusinessAddressRepositoryImpl, BusinessContactDetailsRepositoryImpl, BusinessSpecsRepositoryImpl}
import repositories.desk.DeskListingRepositoryImpl
import repositories.office.{OfficeAddressRepositoryImpl, OfficeContactDetailsRepositoryImpl, OfficeSpecsRepositoryImpl}
import services.*
import services.business.business_listing.BusinessListingServiceImpl
import services.desk_listing.DeskListingServiceImpl
import services.office.office_listing.OfficeListingServiceImpl

object Routes {

  def deskListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val deskListingRepo = new DeskListingRepositoryImpl[F](transactor)

    val deskListingService = new DeskListingServiceImpl[F](deskListingRepo)
    val deskListingController = new DeskListingControllerImpl[F](deskListingService)

    deskListingController.routes
  }

  def officeListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeSpecsRepository = new OfficeSpecsRepositoryImpl[F](transactor)
    val officeAddressRepository = new OfficeAddressRepositoryImpl[F](transactor)
    val officeContactDetailsRepository = new OfficeContactDetailsRepositoryImpl[F](transactor)

    val officeListingService = new OfficeListingServiceImpl[F](officeAddressRepository, officeContactDetailsRepository, officeSpecsRepository)
    val officeListingController = new OfficeListingControllerImpl[F](officeListingService)

    officeListingController.routes
  }

  def businessListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val businessSpecsRepository = new BusinessSpecsRepositoryImpl[F](transactor)
    val businessAddressRepository = new BusinessAddressRepositoryImpl[F](transactor)
    val businessContactDetailsRepository = new BusinessContactDetailsRepositoryImpl[F](transactor)

    val businessListingService = new BusinessListingServiceImpl[F](businessAddressRepository, businessContactDetailsRepository, businessSpecsRepository)
    val businessListingController = new BusinessListingControllerImpl[F](businessListingService)

    businessListingController.routes
  }
}
