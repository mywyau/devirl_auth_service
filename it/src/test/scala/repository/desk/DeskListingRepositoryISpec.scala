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
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import repositories.desk.DeskListingRepositoryImpl
import repository.fragments.desk.DeskPricingRepoFragments.*
import repository.fragments.desk.DeskSpecificationsRepoFragments.*
import shared.TransactorResource
import testData.DeskTestConstants.*
import testData.TestConstants.*
import utils.Diffable
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class DeskListingRepositoryISpec(global: GlobalRead) extends IOSuite with RepositoryISpecBase {

  type Res = DeskListingRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        resetDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        insertDeskSpecifications.update.run.transact(transactor.xa).void *>
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

  test(".findByDeskId() - should return the desk listing details for a given deskId") { businessDeskRepo =>

    val expectedSpecifications =
      DeskSpecificationsPartial(
        deskId = deskId1,
        deskName = deskName1,
        description = Some(description1),
        deskType = Some(PrivateDesk),
        quantity = Some(5),
        features = Some(List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp")),
        openingHours = Some(deskOpeningHours),
        rules = Some(rules)
      )

    val expectedResult =
      DeskListing(
        deskId = deskId1,
        expectedSpecifications,
        sampleRetrievedDeskPricing
      )


    for {
      deskListingOpt <- businessDeskRepo.findByDeskId(deskId1)
      _ = deskListingOpt.foreach(deskListing => Diffable.logDifferences(expectedResult, deskListing))
    } yield expect(deskListingOpt == Some(expectedResult))
  }

  test(".initiate() - should return CreateSuccess for successfuly creating a base desk listing") { businessDeskRepo =>

    val initiateRequest =
      InitiateDeskListingRequest(
        businessId = businessId6,
        officeId = officeId6,
        deskId = deskId6,
        deskName = deskName1,
        description = description1
      )

    for {
      deskListingOpt <- businessDeskRepo.initiate(initiateRequest)
    } yield expect(deskListingOpt == Valid(CreateSuccess))
  }

  test(".delete() - should return DeleteSuccess for successfuly deleting the desk based on the deskId") { businessDeskRepo =>
    for {
      deskListingOpt <- businessDeskRepo.delete(deskId2)
    } yield expect(deskListingOpt == Valid(DeleteSuccess))
  }
}
