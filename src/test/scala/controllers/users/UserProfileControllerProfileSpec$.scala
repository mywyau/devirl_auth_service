package controllers.users

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import controllers.UserProfileController
import controllers.users.mocks.*
import io.circe.syntax.*
import models.users.*
import models.users.responses.*
import org.http4s.*
import org.http4s.Status.{BadRequest, Created, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.auth.algebra.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime


object UserProfileControllerProfileSpec$ extends SimpleIOSuite {

  // Helper to create a test user
  def testUser(username: String): UserProfile =
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        userId = "user_id_1",
        username = username,
        password_hash = "hashed_password"
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
        created_at = LocalDateTime.now()
      ),
      contact_number = "123456789",
      email = "john.doe@example.com",
      role = Wanderer,
      created_at = LocalDateTime.now()
    )

  // Create UserController instance
  def createUserController(authService: AuthenticationServiceAlgebra[IO],
                           registrationService: RegistrationServiceAlgebra[IO],
                           tokenService: TokenServiceAlgebra[IO]
                          ): HttpRoutes[IO] =
    UserProfileController[IO](authService, registrationService, tokenService).routes

  // Test case for POST /register: Success
  test("POST /register should return 201 when user is created successfully") {
    val registrationRequest =
      UserRegistrationRequest(
        userId = "user_id_2",
        username = "newuser",
        password = "password123",
        first_name = "Jane",
        last_name = "Doe",
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        contact_number = "123456789",
        role = Wanderer,
        email = "jane.doe@example.com"
      )

    val validRegisterUserMockResult =
      IO.pure(Valid(
        UserProfile(
          userId = "user_id_1",
          UserLoginDetails(
            userId = "user_id_1",
            username = "test_user",
            password_hash = "hashed_password"
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
            created_at = LocalDateTime.now()
          ),
          contact_number = "123456789",
          email = "john.doe@example.com",
          role = Wanderer,
          created_at = LocalDateTime.now()
        )
      ))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val mockAuthService = MockAuthService(
      loginUserMock = _ => IO.pure(Right(testUser("newuser")))
    )

    val mockTokenService = new MockTokenService

    val controller = createUserController(mockAuthService, mockRegistrationService, mockTokenService)

    val request = Request[IO](Method.POST, uri"/register")
      .withEntity(registrationRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Created)
  }

  // Test case for POST /register: Validation Failure
  test("POST /register should return 400 when validation fails") {

    val registrationRequest =
      UserRegistrationRequest(
        userId = "user_id_1",
        username = "existinguser",
        password = "password123",
        first_name = "Jane",
        last_name = "Doe",
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        contact_number = "123456789",
        email = "jane.doe@example.com",
        role = Wanderer
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

    val request = Request[IO](Method.POST, uri"/register").withEntity(registrationRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[ErrorUserResponse]
    } yield expect.all(
      response.status == BadRequest,
      body.response == List("Username already exists")
    )
  }

  // Test case for POST /login: Success
  test("POST /login should return 200 when login is successful") {

    val loginRequest = UserLoginRequest(username = "validuser", password = "password123")

    val validRegisterUserMockResult =
      IO.pure(Valid(
        UserProfile(
          userId = "user_id_2",
          UserLoginDetails(
            userId = "user_id_2",
            username = "test_user",
            password_hash = "hashed_password"
          ),
          first_name = "John",
          last_name = "Doe",
          UserAddress(
            userId = "user_id_2",
            street = "fake street 1",
            city = "fake city 1",
            country = "UK",
            county = Some("County 1"),
            postcode = "CF3 3NJ",
            created_at = LocalDateTime.now()
          ),
          contact_number = "123456789",
          email = "john.doe@example.com",
          role = Wanderer,
          created_at = LocalDateTime.now()
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
  test("POST /login should return 500 when login fails due to invalid credentials") {

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
