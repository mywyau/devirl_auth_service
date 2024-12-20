package controllers.business.business_address

import cats.effect.IO
import controllers.business.BusinessAddressController
import controllers.business.business_address.constants.BusinessAddressControllerConstants.sampleBusinessAddress1
import controllers.business.business_address.mocks.MockBusinessAddressService
import models.business.address_details.errors.BusinessUserNotFound
import models.business.address_details.responses.error.BusinessAddressErrorResponse
import org.http4s.*
import org.http4s.Status.{BadRequest, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import services.business.address.BusinessAddressServiceAlgebra
import weaver.SimpleIOSuite

object BusinessAddressControllerSpec extends SimpleIOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def createUserController(businessAddressService: BusinessAddressServiceAlgebra[IO]): HttpRoutes[IO] =
    BusinessAddressController[IO](businessAddressService).routes
  
  test("POST - /business/businesses/address/details/ should return 201 when user is created successfully") {

    val mockBusinessAddressService = new MockBusinessAddressService(Map("user_id_1" -> sampleBusinessAddress1))

    val controller = createUserController(mockBusinessAddressService)

    val request = Request[IO](Method.GET, uri"/business/businesses/address/details/user_id_1")

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Ok)
  }

  test("POST - /business/address/details/ - should return 400 when a user id is not found") {

    val mockBusinessAddressService = new MockBusinessAddressService(Map())

    val controller = createUserController(mockBusinessAddressService)

    val request = Request[IO](Method.GET, uri"/business/businesses/address/details/user_id_2")

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[BusinessAddressErrorResponse]
    } yield expect.all(
      response.status == BadRequest,
      body == BusinessAddressErrorResponse(BusinessUserNotFound.code, BusinessUserNotFound.errorMessage)
    )
  }
}
