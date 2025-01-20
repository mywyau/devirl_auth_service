package controllers

import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*
import controllers.business.BusinessAddressController
import controllers.business.BusinessContactDetailsController
import controllers.business.BusinessListingController
import controllers.business.BusinessSpecificationsController
import controllers.desk.DeskListingController
import controllers.desk.DeskPricingController
import controllers.desk.DeskSpecificationsController
import controllers.office.OfficeAddressController
import controllers.office.OfficeContactDetailsController
import controllers.office.OfficeListingController
import controllers.office.OfficeSpecificationsController
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.transactor.Transactor
import doobie.util.ExecutionContexts
import java.time.LocalDateTime
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.business.BusinessAddressRepository
import repositories.business.BusinessContactDetailsRepository
import repositories.business.BusinessListingRepository
import repositories.business.BusinessSpecificationsRepository
import repositories.desk.DeskListingRepository
import repositories.desk.DeskPricingRepository
import repositories.desk.DeskSpecificationsRepository
import repositories.office.OfficeAddressRepository
import repositories.office.OfficeContactDetailsRepository
import repositories.office.OfficeListingRepository
import repositories.office.OfficeSpecificationsRepository
import services.business.BusinessAddressService
import services.business.BusinessContactDetailsService
import services.business.BusinessListingService
import services.business.BusinessSpecificationsService
import services.desk.DeskListingService
import services.desk.DeskPricingService
import services.desk.DeskSpecificationsService
import services.office.OfficeAddressService
import services.office.OfficeContactDetailsService
import services.office.OfficeListingService
import services.office.OfficeSpecificationsService

object TestRoutes {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def businessContactDetailsRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val businessContactDetailsRepository = BusinessContactDetailsRepository(transactor)

    val businessContactDetailsService = BusinessContactDetailsService(businessContactDetailsRepository)
    val businessContactDetailsController = BusinessContactDetailsController(businessContactDetailsService)

    businessContactDetailsController.routes
  }

  def businessAddressRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val businessAddressRepository = BusinessAddressRepository(transactor)

    val businessAddressService = BusinessAddressService(businessAddressRepository)
    val businessAddressController = BusinessAddressController(businessAddressService)

    businessAddressController.routes
  }

  def businessSpecificationsRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val businessSpecificationsRepository = BusinessSpecificationsRepository(transactor)

    val businessSpecificationsService = BusinessSpecificationsService(businessSpecificationsRepository)
    val businessSpecificationsController = BusinessSpecificationsController(businessSpecificationsService)

    businessSpecificationsController.routes
  }

  def businessListingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val businessListingRepository = BusinessListingRepository(transactor)

    val businessListingService = BusinessListingService(businessListingRepository)
    val businessListingController = BusinessListingController(businessListingService)

    businessListingController.routes
  }

  def deskSpecificationsRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val deskSpecificationsRepository = DeskSpecificationsRepository(transactor)
    val deskSpecificationsService = DeskSpecificationsService(deskSpecificationsRepository)
    val deskSpecificationsController = DeskSpecificationsController(deskSpecificationsService)

    deskSpecificationsController.routes
  }

  def deskPricingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val deskPricingRepository = DeskPricingRepository(transactor)
    val deskPricingService = DeskPricingService(deskPricingRepository)
    val deskPricingController = DeskPricingController(deskPricingService)

    deskPricingController.routes
  }

   def deskListingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val deskListingRepository = DeskListingRepository(transactor)
    val deskListingService = DeskListingService(deskListingRepository)
    val deskListingController = DeskListingController(deskListingService)

    deskListingController.routes
  }

  def officeAddressRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val officeAddressRepository = OfficeAddressRepository(transactor)

    val officeAddressService = OfficeAddressService(officeAddressRepository)
    val officeAddressController = OfficeAddressController(officeAddressService)

    officeAddressController.routes
  }

  def officeContactDetailsRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val officeContactDetailsRepository = OfficeContactDetailsRepository(transactor)

    val officeContactDetailsService = OfficeContactDetailsService(officeContactDetailsRepository)
    val officeContactDetailsController = OfficeContactDetailsController(officeContactDetailsService)

    officeContactDetailsController.routes
  }

  def officeSpecificationsRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val officeSpecificationsRepository = OfficeSpecificationsRepository(transactor)

    val officeSpecificationsService = OfficeSpecificationsService(officeSpecificationsRepository)
    val officeSpecificationsController = OfficeSpecificationsController(officeSpecificationsService)

    officeSpecificationsController.routes
  }

  def officeListingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val officeListingRepository = OfficeListingRepository(transactor)
    val officeListingService = OfficeListingService(officeListingRepository)
    val officeListingController = OfficeListingController(officeListingService)

    officeListingController.routes
  }

  def createTestRouter(transactor: Transactor[IO]): HttpRoutes[IO] =
    Router(
      "/pistachio" -> (
        businessAddressRoutes(transactor) <+>
          businessContactDetailsRoutes(transactor) <+>
          businessSpecificationsRoutes(transactor) <+>
          businessListingRoutes(transactor) <+>
          officeAddressRoutes(transactor) <+>
          officeContactDetailsRoutes(transactor) <+>
          officeSpecificationsRoutes(transactor) <+>
          officeListingRoutes(transactor) <+>
          deskSpecificationsRoutes(transactor) <+>
          deskPricingRoutes(transactor) <+>
          deskListingRoutes(transactor)
      )
    )
}
