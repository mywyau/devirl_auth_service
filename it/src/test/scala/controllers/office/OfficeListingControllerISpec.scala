package controllers.office

import cats.effect.*
import controllers.constants.OfficeListingControllerConstants.*
import controllers.fragments.OfficeAddressRepoFragments.*
import controllers.fragments.OfficeContactDetailsRepoFragments.*
import controllers.fragments.OfficeSpecificationRepoFragments.*
import doobie.implicits.*
import io.circe.Json
import io.circe.syntax.*
import models.office.office_listing.OfficeListing
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import shared.{HttpClientResource, TransactorResource}
import weaver.*

class OfficeListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createOfficeAddressTable.update.run.transact(transactor.xa).void *>
          resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
          insertOfficeAddressesTable.update.run.transact(transactor.xa).void *>

          createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          insertOfficeContactDetailsData.update.run.transact(transactor.xa).void *>

          createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
          resetOfficeSpecsTable.update.run.transact(transactor.xa).void *>
          insertOfficeSpecificationsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "POST - /pistachio/business/office/listing/initiate - should generate the office listing data for a business in the respective tables, returning Created response, " +
      "and \nGET - /pistachio/business/office/listing/find/all - should retrieve all of the office listings, including the newly created listing"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val initiateOfficeListingRequest: Json =
      testInitiateOfficeListingRequest("business_id_1", "office_id_1").asJson

    // Step 1: Make the POST request to create the office listing
    val createRequest =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/office/listing/initiate")
        .withEntity(initiateOfficeListingRequest)

    client.run(createRequest).use { createResponse =>
      createResponse.as[OfficeListing].flatMap { createdOfficeListing =>
        expect.all(
          createResponse.status == Status.Created,
          createdOfficeListing.officeId == "office_id_1",
          createdOfficeListing.officeAddressDetails.officeId == "office_id_1",
          createdOfficeListing.officeContactDetails.officeId == "office_id_1",
          createdOfficeListing.officeSpecifications.officeId == "office_id_1",
          createdOfficeListing.officeAddressDetails.city.isEmpty,
          createdOfficeListing.officeContactDetails.contactNumber.isEmpty,
          createdOfficeListing.officeSpecifications.officeType.isEmpty
        )

        // Step 2: Make the GET request to verify all listings include the newly created one
        val findAllRequest =
          Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/office/listing/find/all")

        client.run(findAllRequest).use { findAllResponse =>
          findAllResponse.as[List[OfficeListing]].map { allListings =>
            expect.all(
              findAllResponse.status == Status.Ok,
              allListings.exists(_.officeId == createdOfficeListing.officeId),
              allListings.size == 3,
              allListings.find(_.officeId == "office_id_1").flatMap(_.officeAddressDetails.city).isEmpty,
              allListings.find(_.officeId == "office_id_1").flatMap(_.officeContactDetails.contactNumber).isEmpty,
              allListings.find(_.officeId == "office_id_1").flatMap(_.officeSpecifications.officeType).isEmpty,
            )
          }
        }
      }
    }
  }

}
