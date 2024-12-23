package routes

import cats.NonEmptyParallel
import cats.effect.*
import controllers.*
import controllers.business_listing.BusinessListingControllerImpl
import controllers.desk_listing.DeskListingControllerImpl
import controllers.office.{OfficeAddressController, OfficeContactDetailsController, OfficeSpecificationsController}
import controllers.office_listing.OfficeListingController
import dev.profunktor.redis4cats.effect.Log
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import repositories.*
import repositories.business.{BusinessAddressRepository, BusinessContactDetailsRepository, BusinessSpecificationsRepository}
import repositories.desk.DeskListingRepository
import repositories.office.{OfficeAddressRepository, OfficeContactDetailsRepository, OfficeSpecificationsRepository}
import services.*
import services.business.business_listing.BusinessListingService
import services.desk_listing.DeskListingService
import services.office.OfficeSpecificationsService
import services.office.address.OfficeAddressService
import services.office.contact_details.OfficeContactDetailsService
import services.office.office_listing.OfficeListingService

object Routes {

  def deskListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val deskListingRepo = DeskListingRepository(transactor)
    val deskListingService = DeskListingService(deskListingRepo)
    val deskListingController = new DeskListingControllerImpl[F](deskListingService)

    deskListingController.routes
  }

  def officeAddressRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeAddressRepository = OfficeAddressRepository(transactor)
    val officeAddressService = OfficeAddressService(officeAddressRepository)
    val officeAddressController = OfficeAddressController(officeAddressService)

    officeAddressController.routes
  }

  def officeContactDetailsRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeContactDetailsRepository = OfficeContactDetailsRepository(transactor)
    val officeContactDetailsService = OfficeContactDetailsService(officeContactDetailsRepository)
    val officeContactDetailsController = OfficeContactDetailsController(officeContactDetailsService)

    officeContactDetailsController.routes
  }

  def officeSpecificationsRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeSpecificationsRepository = OfficeSpecificationsRepository(transactor)
    val officeSpecificationsService = OfficeSpecificationsService(officeSpecificationsRepository)
    val officeSpecificationsController = OfficeSpecificationsController(officeSpecificationsService)

    officeSpecificationsController.routes
  }

  def officeListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeSpecificationsRepository = OfficeSpecificationsRepository(transactor)
    val officeAddressRepository = OfficeAddressRepository(transactor)
    val officeContactDetailsRepository = OfficeContactDetailsRepository(transactor)

    val officeListingService = OfficeListingService(officeAddressRepository, officeContactDetailsRepository, officeSpecificationsRepository)
    val officeListingController = OfficeListingController(officeListingService)

    officeListingController.routes
  }

  def businessListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val businessSpecsRepository = BusinessSpecificationsRepository(transactor)
    val businessAddressRepository = BusinessAddressRepository(transactor)
    val businessContactDetailsRepository = BusinessContactDetailsRepository(transactor)

    val businessListingService = BusinessListingService(businessAddressRepository, businessContactDetailsRepository, businessSpecsRepository)
    val businessListingController = new BusinessListingControllerImpl[F](businessListingService)

    businessListingController.routes
  }
}
