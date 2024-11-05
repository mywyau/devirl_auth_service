package controllers.users

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import controllers.UserProfileController
import controllers.users.mocks.*
import io.circe.syntax.*
import models.users.*
import models.users.database.UserLoginDetails
import models.users.requests.UserSignUpRequest
import models.users.responses.*
import org.http4s.*
import org.http4s.Status.{BadRequest, Created, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.auth.algebra.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserProfileControllerSpec extends SimpleIOSuite {

  // Helper to create a test user
  def testUser(username: String): UserProfile =
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        id = Some(1),
        user_id = "user_id_1",
        username = username,
        password_hash = "hashed_password",
        email = "john.doe@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      contact_number = "123456789",
      email = "john.doe@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Create UserController instance
  def createUserController(authService: AuthenticationServiceAlgebra[IO],
                           registrationService: RegistrationServiceAlgebra[IO],
                           tokenService: TokenServiceAlgebra[IO]
                          ): HttpRoutes[IO] =
    UserProfileController[IO](authService, registrationService, tokenService).routes

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
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        )
      ))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService = MockAuthService(
      loginUserMock = _ => IO.pure(Right(testUser("newuser")))
    )

    val mockTokenService = new MockTokenService

    val controller = createUserController(mockAuthService, mockRegistrationService, mockTokenService)

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
      IO.pure(Invalid(List("Username already exists")))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService =
      MockAuthService(
        loginUserMock = _ => IO.pure(Right(testUser("existinguser")))
      )

    val mockTokenService = new MockTokenService
    val controller = createUserController(mockAuthService, mockRegistrationService, mockTokenService)

    val request = Request[IO](Method.POST, uri"/register").withEntity(signUpRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[ErrorUserResponse]
    } yield expect.all(
      response.status == BadRequest,
      body.response == List("Username already exists")
    )
  }

  // Test case for POST /login: Success
  test("POST - /login should return 200 when login is successful") {

    val loginRequest = UserLoginRequest(username = "validuser", password = "password123")

    val validRegisterUserMockResult: IO[Valid[UserLoginDetails]] =
      IO.pure(Valid(
        UserLoginDetails(
          id = Some(2),
          user_id = "user_id_2",
          username = "test_user",
          password_hash = "hashed_password",
          email = "john.doe@example.com",
          role = Wanderer,
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        )
      ))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService = MockAuthService(
      loginUserMock = _ => IO.pure(Right(testUser("validuser")))
    )

    val mockTokenService = new MockTokenService

    val controller = createUserController(mockAuthService, mockRegistrationService, mockTokenService)

    val request = Request[IO](Method.POST, uri"/login")
      .withEntity(loginRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Ok)
  }

  // Test case for POST /login: Failure
  test("POST - /login should return 500 when login fails due to invalid credentials") {

    val loginRequest = UserLoginRequest(username = "invaliduser", password = "wrongpassword")

    val validRegisterUserMockResult =
      IO.pure(Invalid(List("We do not care for this test")))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService = {
      MockAuthService(
        loginUserMock = _ => IO.pure(Left("Cannot login an error occurred"))
      )
    }

    val mockTokenService = new MockTokenService
    val controller = createUserController(mockAuthService, mockRegistrationService, mockTokenService)

    val request =
      Request[IO](Method.POST, uri"/login")
        .withEntity(loginRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[ErrorUserResponse]
    } yield expect.all(
      response.status == BadRequest,
      body.response == List("Invalid username or password")
    )
  }
}
