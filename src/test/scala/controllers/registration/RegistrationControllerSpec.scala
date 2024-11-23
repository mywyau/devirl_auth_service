package controllers.registration

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import io.circe.syntax.*
import models.users.*
import models.users.adts.Wanderer
import models.users.registration.UsernameAlreadyExists
import models.users.registration.responses.error.RegistrationErrorResponse
import models.wanderer.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}
import models.wanderer.wanderer_profile.requests.UserSignUpRequest
import org.http4s.*
import org.http4s.Status.{BadRequest, Created}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import services.authentication.registration.RegistrationServiceAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object RegistrationControllerSpec extends SimpleIOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def testUserLoginDetails(username: String): UserLoginDetails = {
    UserLoginDetails(
      id = Some(1),
      userId = "user_id_1",
      username = username,
      passwordHash = "hashed_password",
      email = "john.doe@example.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def testUserProfile(username: String): UserProfile = {
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        id = Some(1),
        userId = "user_id_1",
        username = username,
        passwordHash = "hashed_password",
        email = "john.doe@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      firstName = "John",
      lastName = "Doe",
      UserAddress(
        userId = "user_id_1",
        street = Some("fake street 1"),
        city = Some("fake city 1"),
        country = Some("UK"),
        county = Some("County 1"),
        postcode = Some("CF3 3NJ"),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      contactNumber = "123456789",
      email = "john.doe@example.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def createRegistrationController(
                           registrationService: RegistrationServiceAlgebra[IO]
                          ): HttpRoutes[IO] =
    RegistrationController[IO](registrationService).routes

  test("POST - /register should return 201 when user is created successfully") {
    val signUpRequest =
      UserSignUpRequest(
        userId = "user_id_2",
        username = "newuser",
        password = "password123",
        role = Wanderer,
        email = "jane.doe@example.com",
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val validRegisterUserMockResult: IO[Valid[UserLoginDetails]] =
      IO.pure(Valid(
        UserLoginDetails(
          id = Some(1),
          userId = "user_id_1",
          username = "test_user",
          passwordHash = "hashed_password",
          email = "john.doe@example.com",
          role = Wanderer,
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        )
      ))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)

    val controller = createRegistrationController(registrationService = mockRegistrationService)

    val request = Request[IO](Method.POST, uri"/register")
      .withEntity(signUpRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
    } yield expect(response.status == Created)
  }

  test("POST - /register should return 400 when validation fails") {

    val signUpRequest =
      UserSignUpRequest(
        userId = "user_id_1",
        username = "existinguser",
        password = "password123",
        email = "jane.doe@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val validRegisterUserMockResult =
      IO.pure(Invalid(List(UsernameAlreadyExists)))

    val mockRegistrationService = new MockRegistrationService(_ => validRegisterUserMockResult)
    
    val controller = createRegistrationController(mockRegistrationService)

    val request = Request[IO](Method.POST, uri"/register").withEntity(signUpRequest.asJson)

    for {
      response <- controller.orNotFound.run(request)
      body <- response.as[RegistrationErrorResponse]
    } yield expect.all(
      response.status == BadRequest,
      body == RegistrationErrorResponse(List("Username already exists"), List(), List())
    )
  }
}
