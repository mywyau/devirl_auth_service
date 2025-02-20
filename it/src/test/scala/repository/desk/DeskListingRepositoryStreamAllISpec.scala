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
import models.desk.deskListing.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.UpdateDeskSpecificationsRequest

import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import repositories.desk.DeskListingRepositoryImpl
import repository.fragments.desk.DeskPricingRepoFragments.*
import repository.fragments.desk.DeskSpecificationsRepoFragments.*
import testData.TestConstants.*
import testData.DeskTestConstants.*
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
        DeskListingCard(deskId1, "Luxury supreme desk", description1),
        DeskListingCard(deskId2, "Luxury supreme desk", description1),
        DeskListingCard(deskId3, "Luxury supreme desk", description1),
        DeskListingCard(deskId4, "Luxury supreme desk", description1),
        DeskListingCard(deskId5, "Luxury supreme desk", description1),
      )

    for {
      deskListings <- deskRepo.streamAllListingCardDetails("officeId1").compile.toList
    } yield expect(deskListings == expectedCards)
  }

  test(".streamAllListingCardDetails() - should return an empty list for a non-existent officeId") { deskRepo =>
    for {
      deskListings <- deskRepo.streamAllListingCardDetails("nonexistent_office").compile.toList
    } yield expect(deskListings.isEmpty)
  }
}
