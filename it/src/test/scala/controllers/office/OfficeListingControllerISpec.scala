package controllers.office

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.constants.OfficeListingControllerConstants.*
import controllers.fragments.OfficeAddressRepoFragments.*
import controllers.fragments.OfficeContactDetailsRepoFragments.*
import controllers.fragments.OfficeSpecificationRepoFragments.*
import controllers.office_listing.OfficeListingController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.office.office_listing.OfficeListing
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.office.{OfficeAddressRepository, OfficeContactDetailsRepository, OfficeSpecificationsRepository}
import services.office.office_listing.OfficeListingService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class OfficeListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createOfficeAddressTable.update.run.transact(transactor.xa).void *>
          resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
          createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
          resetOfficeSpecsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "POST - /pistachio/business/office/listing/initiate - " +
      "should generate the office listing data for a business in the respective tables, returning Created response"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val initiateOfficeListingRequest: Json = testInitiateOfficeListingRequest("business_id_1", "office_id_1").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/office/listing/initiate")
        .withEntity(initiateOfficeListingRequest)

    val expectedOfficeListing = ""

    client.run(request).use { response =>
      response.as[OfficeListing].map { body =>
        expect.all(
          response.status == Status.Created,
          body.officeId == "office_id_1",
          body.officeAddressDetails.officeId == "office_id_1",
          body.officeContactDetails.officeId == "office_id_1",
          body.officeSpecifications.officeId == "office_id_1",
          body.officeAddressDetails.city == None,
          body.officeContactDetails.contactNumber == None,
          body.officeSpecifications.officeType == None
        )
      }
    }
  }
}
