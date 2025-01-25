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
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import repositories.desk.DeskListingRepositoryImpl
import repository.fragments.desk.DeskPricingRepoFragments.*
import repository.fragments.desk.DeskSpecificationsRepoFragments.*
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class DeskListingRepositoryStreamAllISpec(global: GlobalRead) extends IOSuite with RepositoryISpecBase {

  type Res = DeskListingRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        resetDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        insertDeskSpecificationsSameOffice.update.run.transact(transactor.xa).void *>
        createDeskPricingsTable.update.run.transact(transactor.xa).void *>
        resetDeskPricingsTable.update.run.transact(transactor.xa).void *>
        insertDeskPricings.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, DeskListingRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessDeskRepo = new DeskListingRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessDeskRepo

    setup
  }

  test(".streamAllListingCardDetails() - should return desk listing details for the given officeId") { deskRepo =>

    val expectedCards =
      List(
        DeskListingCard("desk001", "Mikey Desk 1", "A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        DeskListingCard("desk002", "Mikey Desk 2", "A shared desk in a collaborative space with easy access to team members."),
        DeskListingCard("desk003", "Mikey Desk 3", "Spacious desk with a view and ample storage for your items."),
        DeskListingCard("desk004", "Mikey Desk 4", "A flexible, hot desk available for use in a dynamic work environment."),
        DeskListingCard("desk005", "Mikey Desk 5", "An executive desk in a quiet, well-lit space designed for high-level work.")
      )

    for {
      deskListings <- deskRepo.streamAllListingCardDetails("office001").compile.toList
    } yield expect(deskListings == expectedCards)
  }

  test(".streamAllListingCardDetails() - should return an empty list for a non-existent officeId") { deskRepo =>
    for {
      deskListings <- deskRepo.streamAllListingCardDetails("nonexistent_office").compile.toList
    } yield expect(deskListings.isEmpty)
  }
}
