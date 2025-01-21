package controllers.desk

import cats.effect.*
import controllers.desk.DeskListingController
import controllers.fragments.desk.DeskPricingControllerFragments.*
import controllers.fragments.desk.DeskSpecificationsControllerFragments.*
import controllers.ControllerISpecBase
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.database.CreateSuccess
import models.database.UpdateSuccess
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.http4s.Method.*
import repositories.desk.DeskListingRepositoryImpl
import services.desk.DeskListingServiceImpl
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*
import models.desk.deskPricing.RetrievedDeskPricing

class DeskListingControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {
  type Res = (TransactorResource, HttpClientResource)
  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
          resetDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
          insertDeskSpecifications.update.run.transact(transactor.xa).void *>
          createDeskPricingsTable.update.run.transact(transactor.xa).void *>
          resetDeskPricingsTable.update.run.transact(transactor.xa).void *>
          insertDeskPricings.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/listing/details/find/desk002 - should get the desk for a given desk id"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/desk002")

    val expectedDeskSpec =
      DeskSpecificationsPartial(
        deskId = "desk002",
        deskName = "Mikey Desk 2",
        description = Some("A shared desk in a collaborative space with easy access to team members."),
        deskType = Some(PrivateDesk),
        quantity = Some(3),
        features = Some(List("Wi-Fi", "Power Outlets", "Whiteboard", "Projector")),
        availability = Some(
          Availability(
            List("Monday", "Wednesday", "Friday"),
            LocalTime.of(9, 0, 0),
            LocalTime.of(17, 0, 0)
          )
        ),
        rules = Some("Respect others' privacy and keep noise levels to a minimum.")
      )

    val expectedDeskPricing =
      RetrievedDeskPricing(
        pricePerHour = Some(10.00),
        pricePerDay = Some(80.00),
        pricePerWeek = None,
        pricePerMonth = Some(1500.00),
        pricePerYear = Some(18000.00)
      )

    val expectedDeskListing =
      DeskListing(
        deskId = "desk002",
        specifications = expectedDeskSpec,
        pricing = expectedDeskPricing
      )

    client.run(request).use { response =>
      response.as[DeskListing].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedDeskListing
        )
      }
    }
  }

  test(
    "GET - /pistachio/business/listing/details/find/desk005 - should get the desk for a given desk id when there are nulls and missing data"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/desk005")

    val expectedDeskSpec =
      DeskSpecificationsPartial(
        deskId = "desk005",
        deskName = "Mikey Desk 5",
        description = Some("An executive desk in a quiet, well-lit space designed for high-level work."),
        deskType = Some(PrivateDesk),
        quantity = None,
        features = None,
        availability =None,
        rules = None
      )

    val expectedDeskPricing =
      RetrievedDeskPricing(
        pricePerHour = None,
        pricePerDay = None,
        pricePerWeek = None,
        pricePerMonth = None,
        pricePerYear = Some(50000.00)
      )

    val expectedDeskListing =
      DeskListing(
        deskId = "desk005",
        specifications = expectedDeskSpec,
        pricing = expectedDeskPricing
      )

    client.run(request).use { response =>
      response.as[DeskListing].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedDeskListing
        )
      }
    }
  }

  test(
    "POST - /pistachio/business/desk/listing/initiate - should create/initiate a base desk listing"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val initiateRequest =
      InitiateDeskListingRequest(
        officeId = "office001",
        deskId = "desk006",
        deskName = "Mikey Desk 1",
        description = "A quiet, private desk perfect for focused work with a comfortable chair and good lighting."
      )

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/initiate")
        .withEntity(initiateRequest.asJson)

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == CreatedResponse(CreateSuccess.toString, "Successfully created an initial desk listing")
        )
      }
    }
  }

  test(
    "GET - /pistachio/business/desk/listing/cards/find/all/office001 - should find all the desk listings for office001 == 2" +
      "\nPOST - /pistachio/business/desk/listing/initiate - should create an initial desk listing card" +
      "\nDELETE - /pistachio/business/desk/listing/details/delete/desk003 - should delete desk with id == desk003"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val initiateRequest =
      InitiateDeskListingRequest(
        officeId = "office001",
        deskId = "desk007",
        deskName = "Mikey Desk 7",
        description = "A quiet, private desk perfect for focused work with a comfortable chair and good lighting."
      )

    val expectedDeskListing = CreatedResponse(CreateSuccess.toString, "Business Desk created successfully")

    val findAllCardsRequest =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/cards/find/all/office001")

    val createRequest =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/initiate")
        .withEntity(initiateRequest.asJson)

    val deleteRequest =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/delete/desk003")

    for {
      findAllResponseBefore <- client.run(findAllCardsRequest).use(_.as[List[DeskListingCard]])
      _ <- IO(expect(findAllResponseBefore.size == 2))
      createResponse <- client.run(createRequest).use(_.as[CreatedResponse])
      _ <- IO(expect(createResponse == expectedDeskListing))
      deleteResponse <- client.run(deleteRequest).use(_.as[DeletedResponse])
      _ <- IO(expect(deleteResponse.message == "Successfully deleted desk listing"))
    } yield success
  }

}
