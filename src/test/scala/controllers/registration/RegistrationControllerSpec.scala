package controllers.registration

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import controllers.users.mocks.*
import io.circe.syntax.*
import models.auth.UsernameAlreadyExists
import models.users.*
import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}
import models.users.wanderer_profile.requests.UserSignUpRequest
import models.users.wanderer_profile.responses.error.RegistrationErrorResponse
import org.http4s.*
import org.http4s.Status.{BadRequest, Created}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.auth.algebra.*
import services.registration.RegistrationServiceAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object RegistrationControllerSpec extends SimpleIOSuite {

  def testUserLoginDetails(username: String): UserLoginDetails = {
    UserLoginDetails(
      id = Some(1),
      user_id = "user_id_1",
      username = username,
      password_hash = "hashed_password",
      email = "john.doe@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  // Helper to create a test user
  def testUserProfile(username: String): UserProfile = {
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        id = Some(1),
        user_id = "user_id_1",
        username = username,
        password_hash = "hashed_password",
        email = "john.doe@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      first_name = "John",
      last_name = "Doe",
      UserAddress(
        userId = "user_id_1",
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      contact_number = "123456789",
      email = "john.doe@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  // Create UserController instance
  def createUserController(authService: AuthenticationServiceAlgebra[IO],
                           registrationService: RegistrationServiceAlgebra[IO]
                          ): HttpRoutes[IO] =
    RegistrationController[IO](registrationService).routes

  // Test case for POST /register: Success
  test("POST - /register should return 201 when user is created successfully") {
    val signUpRequest =
      UserSignUpRequest(
        user_id = "user_id_2",
        username = "newuser",
        password = "password123",
        role = Wanderer,
        email = "jane.doe@example.com",
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val validRegisterUserMockResult: IO[Valid[UserLoginDetails]] =
      IO.pure(Valid(
        UserLoginDetails(
          id = Some(1),
          user_id = "user_id_1",
          username = "test_user",
          password_hash = "hashed_password",
          email = "john.doe@example.com",
          role = Wanderer,
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        )
      ))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService = MockAuthService(
      loginUserMock = _ => IO.pure(Right(testUserLoginDetails("newuser")))
    )

    val mockTokenService = new MockTokenService

    val controller = createUserController(mockAuthService, mockRegistrationService)

    val request = Request[IO](Method.POST, uri"/register")
      .withEntity(signUpRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Created)
  }

  // Test case for POST /register: Validation Failure
  test("POST - /register should return 400 when validation fails") {

    val signUpRequest =
      UserSignUpRequest(
        user_id = "user_id_1",
        username = "existinguser",
        password = "password123",
        email = "jane.doe@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val validRegisterUserMockResult =
      IO.pure(Invalid(List(UsernameAlreadyExists)))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService =
      MockAuthService(
        loginUserMock = _ => IO.pure(Right(testUserLoginDetails("existinguser")))
      )

    val mockTokenService = new MockTokenService
    val controller = createUserController(mockAuthService, mockRegistrationService)

    val request = Request[IO](Method.POST, uri"/register").withEntity(signUpRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[RegistrationErrorResponse]
    } yield expect.all(
      response.status == BadRequest,
      body == RegistrationErrorResponse(List("Username already exists"),List(),List())
    )
  }
}
