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
import models.auth.UserSession
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

class AuthControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {

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

  test("GET - /devirl-auth-service/auth/health -  health check should return the health response") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/devirl-auth-service/auth/health")

    client.run(request).use { response =>
      response.as[GetResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == GetResponse("auth_health_success", "I am alive")
        )
      }
    }
  }

  test("GET - /devirl-auth-service/auth/session/USER001 - for a given user with session stored in cache, return a successful GET response") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val userSession =
      UserSession(
        userId = "USER001",
        cookieValue = "test-session-token",
        email = "USER001@gmail.com",
        userType = "Dev"
      )

    val successfulResponse = GetResponse("200", s"Session token: $userSession")

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/devirl-auth-service/auth/session/USER001")

    client.run(request).use { response =>
      response.as[GetResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == successfulResponse
        )
      }
    }
  }

  test("POST - /devirl-auth-service/auth/session/USER007 - for a given user with session stored in cache, return a successful Created response") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val sessionToken = "test-session-token"

    def testCreateAuth(): CreateUserData =
      CreateUserData(
        username = "Perfect_Cell",
        email = "danny_smith@gmail.com",
        firstName = Some("Danny"),
        lastName = Some("Smith"),
        userType = Some(Client)
      )

    val requestBody: Json = testCreateAuth().asJson
    val expectedBody = CreatedResponse("USER007", "Session stored from cookie in session cache")

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/devirl-auth-service/auth/session/USER007")
        .addCookie("auth_session", sessionToken)
        .withEntity(requestBody)

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBody
        )
      }
    }
  }

  test("POST - /devirl-auth-service/auth/session/sync/USER002 - for a given user with user details data in DB, store the details in the auth session cache and return a successful Created response") {
    (transactorResource, log) =>

      val transactor = transactorResource._1.xa
      val client = transactorResource._2.client

      val sessionToken = "test-session-token"

      def testCreateAuth(): CreateUserData =
        CreateUserData(
          username = "Perfect_Cell",
          email = "danny_smith@gmail.com",
          firstName = Some("Danny"),
          lastName = Some("Smith"),
          userType = Some(Client)
        )

      val requestBody: Json = testCreateAuth().asJson
      val expectedBody = CreatedResponse("USER002", "Session synced from DB")

      val request =
        Request[IO](POST, uri"http://127.0.0.1:9999/devirl-auth-service/auth/session/sync/USER002")
          .addCookie("auth_session", sessionToken)
          .withEntity(requestBody)

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
    "DELETE - /devirl-auth-service/auth/session/delete/USER004 - for a given user with user details in auth session cache, delete the details from the auth session cache. Return a successful OK response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val expectedBody = DeletedResponse("USER004", "Session deleted")

    val getRequest =
      Request[IO](GET, uri"http://127.0.0.1:9999/devirl-auth-service/auth/session/USER004")

    val deleteRequest =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/devirl-auth-service/auth/session/delete/USER004")

    val userSession =
      UserSession(
        userId = "USER004",
        cookieValue = "test-session-token",
        email = "USER004@gmail.com",
        userType = "Dev"
      )

    for {
      initialGetExpect <- client.run(getRequest).use { response =>
        response.as[GetResponse].map { response =>
          expect.all(
            response.code == "200",
            response == GetResponse("200", s"Session token: $userSession")
          )
        }
      }
      deleteExpect <- client.run(deleteRequest).use { response =>
        response.as[DeletedResponse].map { body =>
          expect.all(
            response.status == Status.Ok,
            body == expectedBody
          )
        }
      }
      afterGetExpect <- client.run(getRequest).use { response =>
        response.as[GetResponse].map { response =>
          expect.all(
            response.code == "NOT_FOUND",
            response == GetResponse("NOT_FOUND", s"No session for userId USER004")
          )
        }
      }
    } yield initialGetExpect && deleteExpect && afterGetExpect
  }
}
