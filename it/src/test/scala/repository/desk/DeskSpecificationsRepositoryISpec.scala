package repository

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.database.CreateSuccess
import models.database.DeleteSuccess
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import repositories.desk.DeskSpecificationsRepositoryImpl
import repository.fragments.desk.DeskSpecificationsRepoFragments.*
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

import java.time.LocalDateTime
import java.time.LocalTime

class DeskSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite with RepositoryISpecBase {

  type Res = DeskSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        resetDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        insertDeskSpecifications.update.run.transact(transactor.xa).void
    )

  val availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday"),
      startTime = LocalTime.of(9, 0, 0),
      endTime = LocalTime.of(17, 0, 0)
    )

  def sharedResource: Resource[IO, DeskSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessDeskRepo = new DeskSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessDeskRepo

    setup
  }

  test(".findByDeskId() - should return the desk listing details for a given deskId") { businessDeskRepo =>

    val expectedResult =
      DeskSpecificationsPartial(
        deskId = "desk001",
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = Some(PrivateDesk),
        quantity = Some(5),
        features = Some(List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp")),
        availability = Some(Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        )),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    for {
      deskSpecificationsOpt <- businessDeskRepo.findByDeskId("desk001")
    } yield expect(deskSpecificationsOpt == Some(expectedResult))
  }

  test(".findByOfficeId() - should return the all desk listing for a given officeId") { businessDeskRepo =>

    val expectedResult =
      DeskSpecificationsPartial(
        deskId = "desk001",
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = Some(PrivateDesk),
        quantity = Some(5),
        features = Some(List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp")),
        availability = Some(Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        )),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    for {
      deskSpecificationsOpt <- businessDeskRepo.findByOfficeId("office01")
    } yield expect(deskSpecificationsOpt == List(expectedResult))
  }

  test(".create() - should return CreateSuccess for successfuly creating a desk listing") { businessDeskRepo =>

    val createRequest =
      UpdateDeskSpecificationsRequest(
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = PrivateDesk,
        quantity = 5,
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    for {
      deskSpecificationsOpt <- businessDeskRepo.create(createRequest)
    } yield expect(deskSpecificationsOpt == Valid(CreateSuccess))
  }

  test(".delete() - should return DeleteSuccess for successfuly deleting the desk based on the deskId") { businessDeskRepo =>
    for {
      deskSpecificationsOpt <- businessDeskRepo.delete("desk002")
    } yield expect(deskSpecificationsOpt == Valid(DeleteSuccess))
  }
}
