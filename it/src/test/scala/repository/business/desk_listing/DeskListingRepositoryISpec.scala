package repository.business.desk_listing

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.business.desk_listing.Availability
import models.business.desk_listing.requests.DeskListingRequest
import models.business.desk_listing.service.DeskListing
import repositories.business.DeskListingRepositoryImpl
import repository.fragments.DeskListingRepoFragments.{createDeskListingsTable, resetDeskListingsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class DeskListingRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = DeskListingRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskListingsTable.update.run.transact(transactor.xa).void *>
        resetDeskListingsTable.update.run.transact(transactor.xa).void
    )

  def testDeskListing(id: Option[Int], businessId: String) = {

    val availability =
      Availability(
        days = List("Monday", "Tuesday", "Wednesday"),
        startTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0),
        endTime = LocalDateTime.of(2025, 1, 1, 9, 0, 0)
      )

    DeskListingRequest(
      business_id = businessId,
      workspace_id = "workspace_id_1",
      title = "Quiet Private Desk",
      description = Some("A quiet, private desk for focused work."),
      desk_type = PrivateDesk,
      quantity = 5,
      price_per_hour = 15.50,
      price_per_day = 80.00,
      rules = Some("No loud conversations. Keep the desk clean."),
      features = List("Wi-Fi", "Power Outlets", "Monitor"),
      availability = availability,
      created_at = LocalDateTime.of(2025, 1, 1, 11, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 11, 2, 0)
    )
  }

  private def seedTestAddresses(businessDeskRepo: DeskListingRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testDeskListing(Some(1), "business_id_1"),
      testDeskListing(Some(2), "business_id_2"),
      testDeskListing(Some(3), "business_id_3"),
      testDeskListing(Some(4), "business_id_4"),
      testDeskListing(Some(5), "business_id_5")
    )
    users.traverse(businessDeskRepo.createDeskToRent).void
  }

  def sharedResource: Resource[IO, DeskListingRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessDeskRepo = new DeskListingRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestAddresses(businessDeskRepo))
    } yield businessDeskRepo

    setup
  }

  test(".findByUserId() - should return the desk listing if business_id exists for a previously created booking") { businessDeskRepo =>

    val deskListing = testDeskListing(Some(1), "business_id_1")

    val expectedResult =
      DeskListing(
        id = Some(1),
        business_id = "business_id_1", workspace_id = "workspace_id_1", title = "Quiet Private Desk",
        description = Some("A quiet, private desk for focused work."),
        desk_type = PrivateDesk,
        quantity = 5,
        price_per_hour = 15.50,
        price_per_day = 80.00,
        features = List("Wi-Fi", "Power Outlets", "Monitor"),
        availability = Availability(
          days = List("Monday", "Tuesday", "Wednesday"),
          startTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0),
          endTime = LocalDateTime.of(2025, 1, 1, 9, 0, 0)
        ),
        rules = Some("No loud conversations. Keep the desk clean."),
        created_at = LocalDateTime.of(2025, 1, 1, 11, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 11, 2, 0)
      )

    for {
      deskListingOpt <- businessDeskRepo.findByUserId("business_id_1")
      //      _ <- IO(println(s"Query Result: $deskListingOpt")) // Debug log the result
    } yield expect(deskListingOpt == Some(expectedResult))
  }
}
