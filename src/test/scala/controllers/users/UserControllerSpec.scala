package controllers.users

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import controllers.UserController
import io.circe.syntax.*
import models.users.responses.*
import models.users.{Role, User, UserLoginRequest, UserRegistrationRequest, Wanderer}
import org.http4s.*
import org.http4s.Status.{BadRequest, Created, InternalServerError, Ok}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import services.{AuthenticationService, UserAuth}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserControllerSpec extends SimpleIOSuite {

  // Mock AuthenticationService
  case class MockAuthService(
                              registerUserMock: UserRegistrationRequest => IO[Validated[List[String], User]],
                              loginUserMock: UserLoginRequest => IO[Either[String, User]]
                            ) extends AuthenticationService[IO] {

    override def registerUser(request: UserRegistrationRequest): IO[Validated[List[String], User]] =
      registerUserMock(request)

    override def loginUser(request: UserLoginRequest): IO[Either[String, User]] =
      loginUserMock(request)

    override def authUser(token: String): IO[Option[User]] = IO(None)

    override def authorize(userAuth: UserAuth[IO], requiredRole: Role): IO[Either[String, UserAuth[IO]]] = ???
  }

  // Helper to create a test user
  def testUser(username: String): User =
    User(
      username = username,
      password_hash = "hashed_password",
      first_name = "John",
      last_name = "Doe",
      contact_number = "123456789",
      role = Wanderer,
      email = "john.doe@example.com",
      created_at = LocalDateTime.now()
    )

  // Create UserController instance
  def createUserController(authService: AuthenticationService[IO]): HttpRoutes[IO] =
    UserController[IO](authService).routes

  // Test case for POST /register: Success
  test("POST /register should return 201 when user is created successfully") {
    val registrationRequest =
      UserRegistrationRequest(
        username = "newuser",
        password = "password123",
        first_name = "Jane",
        last_name = "Doe",
        contact_number = "123456789",
        role = Wanderer,
        email = "jane.doe@example.com"
      )

    val mockAuthService = MockAuthService(
      registerUserMock = _ => IO.pure(Valid(testUser("newuser"))),
      loginUserMock = _ => IO.pure(Right(testUser("newuser")))
    )

    val controller = createUserController(mockAuthService)

    val request = Request[IO](Method.POST, uri"/register")
      .withEntity(registrationRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Created)
  }

  // Test case for POST /register: Validation Failure
  test("POST /register should return 400 when validation fails") {
    val registrationRequest = UserRegistrationRequest(
      username = "existinguser",
      password = "password123",
      first_name = "Jane",
      last_name = "Doe",
      contact_number = "123456789",
      role = Wanderer,
      email = "jane.doe@example.com"
    )

    val mockAuthService = MockAuthService(
      registerUserMock = _ => IO.pure(Invalid(List("Username already exists"))),
      loginUserMock = _ => IO.pure(Right(testUser("existinguser")))
    )

    val controller = createUserController(mockAuthService)

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

    val mockAuthService = MockAuthService(
      registerUserMock = _ => IO.pure(Valid(testUser("validuser"))),
      loginUserMock = _ => IO.pure(Right(testUser("validuser")))
    )

    val controller = createUserController(mockAuthService)

    val request = Request[IO](Method.POST, uri"/login")
      .withEntity(loginRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Ok)
  }

  // Test case for POST /login: Failure
  test("POST /login should return 500 when login fails due to invalid credentials") {
    val loginRequest = UserLoginRequest(username = "invaliduser", password = "wrongpassword")

    val mockAuthService =
      MockAuthService(
        registerUserMock = _ => IO.pure(Valid(testUser("invaliduser"))),
        loginUserMock = _ => IO.pure(Left("Cannot login an error occurred"))
      )

    val controller = createUserController(mockAuthService)

    val request = Request[IO](Method.POST, uri"/login")
      .withEntity(loginRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[ErrorUserResponse]
    } yield expect.all(
      response.status == InternalServerError,
      body.response == List("Cannot login an error occurred")
    )
  }
}
