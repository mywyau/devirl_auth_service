package repository

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.database.CreateSuccess
import models.database.DeleteSuccess
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.Availability
import models.desk_listing.DeskListingPartial
import models.desk_listing.PrivateDesk
import repositories.desk.DeskListingRepositoryImpl
import repository.fragments.DeskListingRepoFragments.*
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class DeskListingRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = DeskListingRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskListingsTable.update.run.transact(transactor.xa).void *>
        resetDeskListingsTable.update.run.transact(transactor.xa).void *>
        insertDeskListings.update.run.transact(transactor.xa).void
    )

  val availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday"),
      startTime = LocalTime.of(9, 0, 0),
      endTime = LocalTime.of(17, 0, 0)
    )

  def sharedResource: Resource[IO, DeskListingRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessDeskRepo = new DeskListingRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessDeskRepo

    setup
  }

  test(".findByDeskId() - should return the desk listing details for a given deskId") { businessDeskRepo =>

    val expectedResult =
      DeskListingPartial(
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = PrivateDesk,
        quantity = 5,
        pricePerHour = 20.00,
        pricePerDay = 100.00,
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    for {
      deskListingOpt <- businessDeskRepo.findByDeskId("desk001")
    } yield expect(deskListingOpt == Some(expectedResult))
  }

  test(".findByOfficeId() - should return the all desk listing for a given officeId") { businessDeskRepo =>

    val expectedResult =
      DeskListingPartial(
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = PrivateDesk,
        quantity = 5,
        pricePerHour = 20.00,
        pricePerDay = 100.00,
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    for {
      deskListingOpt <- businessDeskRepo.findByOfficeId("office01")
    } yield expect(deskListingOpt == List(expectedResult))
  }

  test(".create() - should return CreateSuccess for successfuly creating a desk listing") { businessDeskRepo =>

    val createRequest =
      DeskListingRequest(
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = PrivateDesk,
        quantity = 5,
        pricePerHour = 20.00,
        pricePerDay = 100.00,
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    for {
      deskListingOpt <- businessDeskRepo.create(createRequest)
    } yield expect(deskListingOpt == Valid(CreateSuccess))
  }

  test(".delete() - should return DeleteSuccess for successfuly deleting the desk based on the deskId") { businessDeskRepo =>
    for {
      deskListingOpt <- businessDeskRepo.delete("desk002")
    } yield expect(deskListingOpt == Valid(DeleteSuccess))
  }
}
