package controllers

import cats.effect.*
import controllers.fragments.UserDataControllerFragments.*
import controllers.ControllerISpecBase
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import io.circe.Json
import java.time.LocalDateTime
import models.*
import models.database.*
import models.responses.*
import models.users.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.http4s.Method.*
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repository.fragments.UserRepoFragments.createUserTable
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

class RegistrationControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createUserDataTable.update.run.transact(transactor.xa).void *>
          resetUserDataTable.update.run.transact(transactor.xa).void *>
          insertUserData.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /devirl-auth-service/registration/health -  health check should return the health response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/devirl-auth-service/registration/health")

    client.run(request).use { response =>
      response.as[GetResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == GetResponse("/devirl-auth-service/registration/health", "I am alive - RegistrationControllerImpl")
        )
      }
    }
  }

  test(
    "POST - /devirl-auth-service/registration/account/data/create/USER007 - should generate the user data in db table, returning Created response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val sessionToken = "test-session-token"

    def testCreateRegistration(): CreateUserData =
      CreateUserData(
        username = "Perfect_Cell",
        email = "danny_smith@gmail.com",
        firstName = Some("Danny"),
        lastName = Some("Smith"),
        userType = Some(Client)
      )

    val requestBody: Json = testCreateRegistration().asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/devirl-auth-service/registration/account/data/create/USER007")
        .addCookie("auth_session", sessionToken)
        .withEntity(requestBody)

    val expectedBody = CreatedResponse(code = UpdateSuccess.toString(), message = "user details created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBody
        )
      }
    }
  }

  test(
    "PUT - /devirl-auth-service/registration/account/data/update/USER008 - " +
      "given a valid user_id should update the user type for given user - returning Updated response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val sessionToken = "test-session-token"

    val updateUserTypeRequest: RegistrationData =
      RegistrationData(
        username = "videl2",
        firstName = "bob",
        lastName = "smith",
        userType = Client
      )

    val request =
      Request[IO](PUT, uri"http://127.0.0.1:9999/devirl-auth-service/registration/account/data/update/USER008")
        .addCookie("auth_session", sessionToken)
        .withEntity(updateUserTypeRequest.asJson)

    val expectedBody = UpdatedResponse(UpdateSuccess.toString, "User USER008 updated successfully")

    client.run(request).use { response =>
      response.as[UpdatedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBody
        )
      }
    }
  }
}
