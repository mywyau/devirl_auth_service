package controllers.registration

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.users.*
import models.users.adts.Wanderer
import models.users.registration.responses.error.RegistrationErrorResponse
import models.users.wanderer_profile.requests.UserSignUpRequest
import models.users.wanderer_profile.responses.CreatedUserResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import repositories.user_profile.{UserLoginDetailsRepositoryImpl, WandererAddressRepositoryImpl, WandererPersonalDetailsRepositoryImpl}
import services.authentication.password.PasswordServiceImpl
import services.authentication.registration.RegistrationServiceImpl
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class RegistrationControllerISpec(global: GlobalRead) extends IOSuite {

  type Res = (TransactorResource, HttpClientResource)

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {
    val passwordService = new PasswordServiceImpl[IO]()
    val userLoginDetailsRepo = new UserLoginDetailsRepositoryImpl[IO](transactor)
    val wandererAddressRepo = new WandererAddressRepositoryImpl[IO](transactor)
    val wandererPersonalDetailsRepo = new WandererPersonalDetailsRepositoryImpl[IO](transactor)
    val registrationService = new RegistrationServiceImpl[IO](userLoginDetailsRepo, wandererAddressRepo, wandererPersonalDetailsRepo, passwordService)
    val registrationController = RegistrationController(registrationService)

    Router(
      "/cashew" -> registrationController.routes
    )
  }

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        sql"""
          CREATE TABLE IF NOT EXISTS user_login_details (
            id BIGSERIAL PRIMARY KEY,
            user_id VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            password_hash TEXT NOT NULL,
            email VARCHAR(255) NOT NULL,
            role VARCHAR(50) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          )
        """.update.run.transact(transactor.xa).void
      )
      _ <- Resource.eval(
        sql"TRUNCATE TABLE user_login_details RESTART IDENTITY"
          .update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }


  test("POST - /cashew/register should create a new user") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val signupRequest =
      UserSignUpRequest(
        userId = "user_id",
        username = "newuser",
        password = "ValidPass123@",
        email = "newuser@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ).asJson

    val request = Request[IO](POST, uri"http://127.0.0.1:9999/cashew/register").withEntity(signupRequest)

    client.run(request).use { response =>
      response.as[CreatedUserResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body.response == "User created successfully"
        )
      }
    }
  }

  test("POST /register/cashew - attempting to register duplicate users should return an ErrorUserResponse") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val signupRequest =
      UserSignUpRequest(
        userId = "user_1337",
        username = "newuser_1337",
        password = "ValidPass123@",
        email = "newuser_1337@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ).asJson

    val request = Request[IO](POST, uri"http://127.0.0.1:9999/cashew/register").withEntity(signupRequest)

    for {
      // First request - should create the user successfully
      firstResponse <- client.run(request).use { response =>
        response.as[CreatedUserResponse].map { body =>
          expect.all(
            response.status == Status.Created,
            body.response == "User created successfully"
          )
        }
      }

      // Second request - should fail with a duplicate user error
      secondResponse <- client.run(request).use { response =>
        response.as[RegistrationErrorResponse].map { body =>
          expect.all(
            response.status == Status.BadRequest,
            body == RegistrationErrorResponse(List("Username already exists"), List(), List("Email already exists"))
          )
        }
      }
    } yield firstResponse and secondResponse
  }

}
