package controllers

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.users.*
import models.users.responses.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Server
import repositories.users.UserProfileRepositoryImpl
import services.PasswordServiceImpl
import services.auth.*
import services.auth.algebra.*
import weaver.*

import java.time.Instant

class UserProfileControllerISpec(global: GlobalRead) extends IOSuite {

  type Res = (TransactorResource, HttpClientResource)

  // Method to create the Ember server resource
  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  // Shared resource setup: includes the server and client
  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        sql"""
          CREATE TABLE IF NOT EXISTS user_profile (
            id BIGSERIAL PRIMARY KEY,
            userId VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            password_hash TEXT NOT NULL,
            first_name VARCHAR(255) NOT NULL,
            last_name VARCHAR(255) NOT NULL,
            street VARCHAR(255) NOT NULL,
            city VARCHAR(255) NOT NULL,
            country VARCHAR(255) NOT NULL,
            county VARCHAR(255),
            postcode VARCHAR(255) NOT NULL,
            contact_number VARCHAR(100) NOT NULL,
            email VARCHAR(255) NOT NULL,
            role VARCHAR(50) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          )
        """.update.run.transact(transactor.xa).void
      )
      _ <- Resource.eval(sql"DELETE FROM user_profile".update.run.transact(transactor.xa).void)
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }

  object MockTokenService extends TokenServiceAlgebra[IO] {
    override def invalidateToken(token: String): IO[Boolean] = IO.pure(token == "valid_token")

    override def refreshAccessToken(token: String): IO[Option[String]] =
      IO.pure(if (token == "refresh_token") Some("new_token") else None)

    override def createToken(user: UserProfile, expiration: Instant): IO[String] = IO.pure("mock_token")

    override def validateToken(token: String): IO[Either[TokenStatus, String]] = IO.pure(Right("user_id"))
  }

  // Set up actual service implementations using the transactor resource
  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {
    val passwordService = new PasswordServiceImpl[IO]()
    val userRepository = new UserProfileRepositoryImpl[IO](transactor)
    val authService = new AuthenticationServiceImpl[IO](userRepository, passwordService)
    val registrationService = new RegistrationServiceImpl[IO](userRepository, passwordService)
    val tokenService = MockTokenService
    val userController = UserProfileController(authService, registrationService, tokenService)
    userController.routes
  }

  // Test: POST /register should create a new user
  test("POST /register should create a new user") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val userRequest = UserRegistrationRequest(
      userId = "user_id",
      username = "newuser",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 3NJ",
      contact_number = "1234567890",
      email = "newuser@example.com",
      role = Wanderer
    ).asJson

    val request = Request[IO](POST, uri"http://127.0.0.1:9999/register").withEntity(userRequest)

    client.run(request).use { response =>
      response.as[LoginResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body.response == "User created successfully"
        )
      }
    }
  }
  test("POST /register - attempting to register duplicate users should return an ErrorUserResponse") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val userRequest = UserRegistrationRequest(
      userId = "user_id_1337",
      username = "newuser_1337",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 ZZZ",
      contact_number = "12345678901337",
      email = "newuser_1337@example.com",
      role = Wanderer
    ).asJson

    val request = Request[IO](POST, uri"http://127.0.0.1:9999/register").withEntity(userRequest)

    for {
      // First request - should create the user successfully
      firstResponse <- client.run(request).use { response =>
        response.as[LoginResponse].map { body =>
          expect.all(
            response.status == Status.Created,
            body.response == "User created successfully"
          )
        }
      }

      // Second request - should fail with a duplicate user error
      secondResponse <- client.run(request).use { response =>
        response.as[ErrorUserResponse].map { body =>
          expect.all(
            response.status == Status.BadRequest,
            body.response == List("username already exists", "contact_number already exists", "email already exists")
          )
        }
      }
    } yield firstResponse and secondResponse
  }

}
