package controllers.login

import cats.data.Validated
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.fragments.LoginControllerFragments.{createUserLoginDetailsTable, insertWandererLoginDetailsTable, resetUserLoginDetailsTable}
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.responses.ErrorResponse
import models.users.*
import models.users.login.adts.{LoginPasswordIncorrect, UsernameNotFound}
import models.users.login.errors.LoginErrorResponse
import models.users.login.requests.UserLoginRequest
import models.users.login.responses.LoginResponse
import models.users.registration.RegisterPasswordErrors
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import repositories.user_profile.UserLoginDetailsRepositoryImpl
import services.authentication.login.LoginServiceImpl
import services.authentication.password.PasswordServiceAlgebra
import shared.{HttpClientResource, TransactorResource}
import weaver.*

class LoginControllerISpec(global: GlobalRead) extends IOSuite {

  type Res = (TransactorResource, HttpClientResource)

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createUserLoginDetailsTable.update.run.transact(transactor.xa).void *>
          resetUserLoginDetailsTable.update.run.transact(transactor.xa).void *>
          insertWandererLoginDetailsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }

  object MockPasswordService extends PasswordServiceAlgebra[IO] {

    override def hashPassword(password: String): IO[String] = IO.pure(password)

    override def checkPassword(password: String, hash: String): IO[Boolean] = IO.pure(true)

    override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] = ???
  }

  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[IO](transactor)

    val passwordService = MockPasswordService // mocking out password service due to password hashing

    val loginService = new LoginServiceImpl[IO](userLoginDetailsRepository, passwordService)

    val loginController = LoginController(loginService)

    Router(
      "/cashew" -> loginController.routes
    )
  }

  test("POST - /cashew/login - when given valid login details should allow the user to successfully login") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val loginRequest =
      UserLoginRequest(
        username = "mikey5922",
        password = "hashed_password" // providing an exact match to the hashed password in the db to bypass password hashing
      ).asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/cashew/login")
        .withEntity(loginRequest)

    client.run(request).use { response =>
      response.as[LoginResponse].map { body =>
        expect.all(
          response.status == Status.Ok
          //          body == "User created successfully"
        )
      }
    }
  }

  test("POST /register/cashew - attempting to login with invalid username should throw a BadRequest - LoginErrorResponse") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val loginRequest =
      UserLoginRequest(
        username = "bad name",
        password = "hashed_password" // providing an exact match to the hashed password in the db to bypass password hashing
      ).asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/cashew/login")
        .withEntity(loginRequest)

    client.run(request).use { response =>
      response.as[LoginErrorResponse].map { body =>
        expect.all(
          response.status == Status.BadRequest,
          body.usernameErrors == List(ErrorResponse(UsernameNotFound.code, UsernameNotFound.message)),
          body.passwordErrors == List()
        )
      }
    }
  }

  test("POST /register/cashew - attempting to login with invalid password should throw a BadRequest - LoginErrorResponse") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val loginRequest =
      UserLoginRequest(
        username = "mikey5922",
        password = "invalid_unhashed_password" // providing an exact match to the hashed password in the db to bypass password hashing
      ).asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/cashew/login")
        .withEntity(loginRequest)

    client.run(request).use { response =>
      response.as[LoginErrorResponse].map { body =>
        expect.all(
          response.status == Status.BadRequest,
          body.usernameErrors == List(),
          body.passwordErrors == List(ErrorResponse(LoginPasswordIncorrect.code, LoginPasswordIncorrect.message))
        )
      }
    }
  }
}
