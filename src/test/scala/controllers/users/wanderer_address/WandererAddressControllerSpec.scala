package controllers.users.wanderer_address

import cats.effect.IO
import controllers.users.wanderer_address.constants.WandererAddressControllerConstants.sampleWandererAddress1
import controllers.users.wanderer_address.mocks.MockWandererAddressService
import controllers.wanderer_address.WandererAddressController
import models.users.*
import models.users.wanderer_address.errors.UserNotFound
import models.users.wanderer_address.responses.error.WandererAddressErrorResponse
import org.http4s.*
import org.http4s.Status.{BadRequest, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.wanderer_address.WandererAddressServiceAlgebra
import weaver.SimpleIOSuite

object WandererAddressControllerSpec extends SimpleIOSuite {

  def createUserController(wandererAddressService: WandererAddressServiceAlgebra[IO]): HttpRoutes[IO] =
    WandererAddressController[IO](wandererAddressService).routes
  
  test("POST - /wanderer/address/details/ should return 201 when user is created successfully") {

    val mockWandererAddressService = new MockWandererAddressService(Map("user_id_1" -> sampleWandererAddress1))

    val controller = createUserController(mockWandererAddressService)

    val request = Request[IO](Method.GET, uri"/wanderer/address/details/user_id_1")

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Ok)
  }

  test("POST - /wanderer/address/details/ - should return 400 when a user id is not found") {

    val mockWandererAddressService = new MockWandererAddressService(Map())

    val controller = createUserController(mockWandererAddressService)

    val request = Request[IO](Method.GET, uri"/wanderer/address/details/user_id_2")

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[WandererAddressErrorResponse]
    } yield expect.all(
      response.status == BadRequest,
      body == WandererAddressErrorResponse(UserNotFound.code, UserNotFound.errorMessage)
    )
  }
}
