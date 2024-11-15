package controllers.users.wanderer_profile

import cats.effect.IO
import controllers.users.wanderer_profile.constants.WandererUserProfileControllerConstants.sampleWandererUserProfile1
import controllers.users.wanderer_profile.mocks.MockWandererProfileService
import controllers.wanderer_profile.WandererProfileController
import models.users.*
import models.users.wanderer_profile.errors.UserIdNotFound
import models.users.wanderer_profile.responses.error.{ErrorResponse, WandererProfileErrorResponse}
import org.http4s.*
import org.http4s.Status.{BadRequest, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.wanderer_profile.WandererProfileServiceAlgebra
import weaver.SimpleIOSuite

object WandererProfileControllerSpec extends SimpleIOSuite {

  def createUserController(wandererAddressService: WandererProfileServiceAlgebra[IO]): HttpRoutes[IO] =
    WandererProfileController[IO](wandererAddressService).routes

  test("POST - /wanderer/user/profile/user_id_1 should return 200 when a user details are retrieved") {

    val mockWandererUserProfileService = new MockWandererProfileService(Map("user_id_1" -> sampleWandererUserProfile1))

    val controller = createUserController(mockWandererUserProfileService)

    val request = Request[IO](Method.GET, uri"/wanderer/user/profile/user_id_1")

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Ok)
  }

  test("POST - /wanderer/user/profile/user_id_2 - should return 400 when a user id is not found") {

    val mockWandererUserProfileService = new MockWandererProfileService(Map())

    val controller = createUserController(mockWandererUserProfileService)

    val request = Request[IO](Method.GET, uri"/wanderer/user/profile/user_id_2")

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[WandererProfileErrorResponse]
    } yield expect.all(
      response.status == BadRequest,
      body ==
        WandererProfileErrorResponse(
          loginDetailsErrors = List(),
          addressErrors = List(),
          contactDetailsErrors = List(),
          otherErrors = List(ErrorResponse(UserIdNotFound.code, UserIdNotFound.message))
        )
    )
  }
}
