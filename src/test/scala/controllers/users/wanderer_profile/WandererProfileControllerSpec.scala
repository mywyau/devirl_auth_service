package controllers.users.wanderer_profile

import cats.effect.IO
import controllers.users.wanderer_profile.constants.WandererUserProfileControllerConstants.sampleWandererUserProfile1
import controllers.users.wanderer_profile.mocks.MockWandererProfileService
import controllers.wanderer_profile.WandererProfileController
import models.users.*
import models.users.adts.Admin
import models.users.wanderer_profile.errors.UserIdNotFound
import models.users.wanderer_profile.profile.WandererUserProfile
import models.users.wanderer_profile.requests.{UpdateAddress, UpdateLoginDetails, UpdatePersonalDetails, UpdateProfileRequest}
import models.users.wanderer_profile.responses.error.{ErrorResponse, WandererProfileErrorResponse}
import org.http4s.*
import org.http4s.Status.{BadRequest, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.wanderer_profile.WandererProfileServiceAlgebra
import weaver.SimpleIOSuite
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object WandererProfileControllerSpec extends SimpleIOSuite {

  def createWandererProfileController(wandererAddressService: WandererProfileServiceAlgebra[IO]): HttpRoutes[IO] =
    WandererProfileController[IO](wandererAddressService).routes

  test("POST - /wanderer/user/profile/user_id_1 should return 200 when a user details are retrieved") {

    val mockWandererUserProfileService = new MockWandererProfileService(Map("user_id_1" -> sampleWandererUserProfile1))

    val controller = createWandererProfileController(mockWandererUserProfileService)

    val request = Request[IO](Method.GET, uri"/wanderer/user/profile/user_id_1")

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Ok)
  }

  test("POST - /wanderer/user/profile/user_id_2 - should return 400 when a user id is not found") {

    val mockWandererUserProfileService = new MockWandererProfileService(Map())

    val controller = createWandererProfileController(mockWandererUserProfileService)

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

  test("PUT - /wanderer/user/profile/user_id_1 - should return 200 when the user profile is updated successfully") {

    val updateRequest = 
      UpdateProfileRequest(
      loginDetails = Some(UpdateLoginDetails(
        username = Some("updated_username"),
        passwordHash = Some("new_hashed_password"),
        email = Some("updated_email@example.com"),
        role = Some(Admin)
      )),
      address = Some(UpdateAddress(
        street = Some("456 Updated Street"),
        city = Some("Updated City"),
        country = Some("Updated Country"),
        county = Some("Updated County"),
        postcode = Some("UPDATED123")
      )),
      personalDetails = Some(UpdatePersonalDetails(
        contactNumber = Some("9876543210"),
        firstName = Some("Updated John"),
        lastName = Some("Updated Doe"),
        email = Some("updated.john@example.com"),
        company = Some("Updated Corp")
      ))
    )

    val mockWandererUserProfileService = new MockWandererProfileService(Map("user_id_1" -> sampleWandererUserProfile1))

    val controller = createWandererProfileController(mockWandererUserProfileService)

    val request = Request[IO](Method.PUT, uri"/wanderer/user/profile/user_id_1").withEntity(updateRequest)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[WandererUserProfile]
    } yield expect.all(
      response.status == Ok,
      body.userLoginDetails.exists(_.username == "updated_username"),
      body.userAddress.exists(_.street.contains("456 Updated Street")),
      body.userPersonalDetails.exists(_.contact_number.contains("9876543210"))
    )
  }

}
