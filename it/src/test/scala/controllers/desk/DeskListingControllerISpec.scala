package controllers.desk

import cats.effect.*
import controllers.ControllerISpecBase
import controllers.constants.DeskListingControllerConstants.testDeskListingRequest
import controllers.desk_listing.DeskListingController
import controllers.fragments.DeskListingControllerFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.database.CreateSuccess
import models.database.UpdateSuccess
import models.desk_listing.Availability
import models.desk_listing.DeskListingPartial
import models.desk_listing.PrivateDesk
import models.desk_listing.requests.DeskListingRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.implicits.*
import repositories.desk.DeskListingRepositoryImpl
import services.desk_listing.DeskListingServiceImpl
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

import java.time.LocalDateTime
import java.time.LocalTime

class DeskListingControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {
  type Res = (TransactorResource, HttpClientResource)
  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createDeskListingsTable.update.run.transact(transactor.xa).void *>
          resetDeskListingTable.update.run.transact(transactor.xa).void *>
          insertDeskListings.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/desk/listing/details/find/desk002 - should get the desk for a given desk id"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/desk002")

    val expectedDeskListing =
      DeskListingPartial(
        deskName = "Mikey Desk 2",
        description = Some("A shared desk in a collaborative space with easy access to team members."),
        deskType = PrivateDesk,
        quantity = 3,
        pricePerHour = 18.0,
        pricePerDay = 90.0,
        features = List("Wi-Fi", "Power Outlets", "Whiteboard", "Projector"),
        availability = Availability(
          List("Monday", "Wednesday", "Friday"),
          LocalTime.of(9, 0, 0),
          LocalTime.of(17, 0, 0)
        ),
        rules = Some("Respect others' privacy and keep noise levels to a minimum.")
      )

    client.run(request).use { response =>
      response.as[DeskListingPartial].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedDeskListing
        )
      }
    }
  }

  test(
    "PUT - /pistachio/business/desk/listing/details/update/desk001 - should update the desk for a given desk id"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val updateRequest =
      DeskListingRequest(
        deskName = "Updated desk 1",
        description = Some("Updated desk listing"),
        deskType = PrivateDesk,
        quantity = 5,
        pricePerHour = 20.0,
        pricePerDay = 100.0,
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          List("Monday", "Tuesday", "Wednesday"),
          LocalTime.of(9, 0, 0),
          LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    val request =
      Request[IO](PUT, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/update/desk001")
        .withEntity(updateRequest.asJson)

    client.run(request).use { response =>
      response.as[UpdatedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == UpdatedResponse(UpdateSuccess.toString, "desk listing updated successfully")
        )
      }
    }
  }

  test(
    "GET - /pistachio/business/desk/listing/details/find/all/office01 - should find all the desk listings for office01" +
      "\nPOST - /pistachio/business/desk/listing/create - should create a desk listing" +
      "\nDELETE - /pistachio/business/desk/listing/details/delete/desk003 - should delete a desk listing for a given deskId"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val findAllRequest =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/all/office01")

    val createRequest =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/create")
        .withEntity(testDeskListingRequest.asJson)

    val deleteRequest =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/delete/desk003")

    val expectedDeskListing = CreatedResponse(CreateSuccess.toString, "Business Desk created successfully")

    for {
      findAllResponseBefore <- client.run(findAllRequest).use(_.as[List[DeskListingPartial]])
      _ <- IO(expect(findAllResponseBefore.size == 5))
      createResponse <- client.run(createRequest).use(_.as[CreatedResponse])
      deleteResponse <- client.run(deleteRequest).use(_.as[DeletedResponse])
      _ <- IO(expect(deleteResponse.message == "Successfully deleted desk listing"))
      findAllResponseAfter <- client.run(findAllRequest).use(_.as[List[DeskListingPartial]])
      _ <- IO(expect(findAllResponseAfter.isEmpty))
    } yield success

  }
}
