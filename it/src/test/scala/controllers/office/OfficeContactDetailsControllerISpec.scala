package controllers.office

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import configuration.models.AppConfig
import controllers.constants.OfficeContactDetailsControllerConstants.*
import controllers.fragments.OfficeContactDetailsRepoFragments.{createOfficeContactDetailsTable, insertOfficeContactDetailsData, resetOfficeContactDetailsTable}
import controllers.office.OfficeContactDetailsController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.office.adts.*
import models.office.contact_details.OfficeContactDetails
import models.responses.{CreatedResponse, DeletedResponse}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class OfficeContactDetailsControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          insertOfficeContactDetailsData.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "GET - /pistachio/business/offices/contact/details/OFF001 - " +
      "given a office_id, find the office contact details data for given id, returning OK and the contact json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/offices/contact/details/OFF001")

    val expectedOfficeContactDetails = aliceContactDetails(Some(1), "BUS12345", "OFF001")

    client.run(request).use { response =>
      response.as[OfficeContactDetails].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedOfficeContactDetails
        )
      }
    }
  }

  test(
    "POST - /pistachio/business/offices/contact/details/create - " +
      "should generate the office contact details data in postgresql, returning Created response"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val businessListingRequest: Json = createNewContactDetailsRequest("BUSINESS1337", "OFFICE1337").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/offices/contact/details/create")
        .withEntity(businessListingRequest)

    val expectedBody = CreatedResponse("Office contact details created successfully")

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
    "DELETE - /pistachio/business/offices/contact/details/OFF002 - " +
      "given a office_id, delete the office contact details data for given office id, returning OK and Deleted response json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/offices/contact/details/OFF002")

    val expectedBody = DeletedResponse("Office contact details deleted successfully")

    client.run(request).use { response =>
      response.as[DeletedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBody
        )
      }
    }
  }
}
