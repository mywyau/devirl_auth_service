package controllers.desk

import cats.effect.*
import controllers.ControllerISpecBase
import controllers.desk.DeskSpecificationsController
import controllers.fragments.desk.DeskSpecificationsControllerFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import controllers.constants.DeskSpecificationsControllerConstants.*
import models.database.CreateSuccess
import models.database.UpdateSuccess
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.implicits.*
import repositories.desk.DeskSpecificationsRepositoryImpl
import services.desk.DeskSpecificationsServiceImpl
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

import java.time.LocalDateTime
import java.time.LocalTime

class DeskSpecificationsControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {
  type Res = (TransactorResource, HttpClientResource)
  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createDeskSpecificationssTable.update.run.transact(transactor.xa).void *>
          resetDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
          insertDeskSpecificationss.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/desk/specifications/details/find/desk002 - should get the desk for a given desk id"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/specifications/details/find/desk002")

    val expectedDeskSpecifications =
      DeskSpecificationsPartial(
        deskId = "desk002",
        deskName = "Mikey Desk 2",
        description = Some("A shared desk in a collaborative space with easy access to team members."),
        deskType = PrivateDesk,
        quantity = 3,
        features = List("Wi-Fi", "Power Outlets", "Whiteboard", "Projector"),
        availability = Availability(
          List("Monday", "Wednesday", "Friday"),
          LocalTime.of(9, 0, 0),
          LocalTime.of(17, 0, 0)
        ),
        rules = Some("Respect others' privacy and keep noise levels to a minimum.")
      )

    client.run(request).use { response =>
      response.as[DeskSpecificationsPartial].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedDeskSpecifications
        )
      }
    }
  }

  test(
    "PUT - /pistachio/business/desk/specifications/details/update/desk001 - should update the desk for a given desk id"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val updateRequest =
      UpdateDeskSpecificationsRequest(
        deskName = "Updated desk 1",
        description = Some("Updated desk specifications"),
        deskType = PrivateDesk,
        quantity = 5, 
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          List("Monday", "Tuesday", "Wednesday"),
          LocalTime.of(9, 0, 0),
          LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    val request =
      Request[IO](PUT, uri"http://127.0.0.1:9999/pistachio/business/desk/specifications/details/update/desk001")
        .withEntity(updateRequest.asJson)

    client.run(request).use { response =>
      response.as[UpdatedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == UpdatedResponse(UpdateSuccess.toString, "desk specifications updated successfully")
        )
      }
    }
  }

  test(
    "GET - /pistachio/business/desk/specifications/details/find/all/office001 - should find all the desk specifications for office01" +
      "\nPOST - /pistachio/business/desk/specifications/create - should create a desk specifications" +
      "\nDELETE - /pistachio/business/desk/specifications/details/delete/desk003 - should delete a desk specifications for a given deskId"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val findAllRequest =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/specifications/details/find/all/office001")

    val createRequest =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/desk/specifications/details/create")
        .withEntity(testDeskSpecificationsRequest.asJson)

    val deleteRequest =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/desk/specifications/details/delete/desk003")

    val expectedDeskSpecifications = CreatedResponse(CreateSuccess.toString, "Business Desk created successfully")

    for {
      findAllResponseBefore <- client.run(findAllRequest).use(_.as[List[DeskSpecificationsPartial]])
      _ <- IO(expect(findAllResponseBefore.size == 5))
      createResponse <- client.run(createRequest).use(_.as[CreatedResponse])
      deleteResponse <- client.run(deleteRequest).use(_.as[DeletedResponse])
      _ <- IO(expect(deleteResponse.message == "Successfully deleted desk specifications"))
      findAllResponseAfter <- client.run(findAllRequest).use(_.as[List[DeskSpecificationsPartial]])
      _ <- IO(expect(findAllResponseAfter.isEmpty))
    } yield success

  }
}
