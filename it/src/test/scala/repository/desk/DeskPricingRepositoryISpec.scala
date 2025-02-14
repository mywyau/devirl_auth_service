package repository

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.database.DeleteSuccess
import models.database.UpdateSuccess
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskPricing.UpdateDeskPricingRequest
import repositories.desk.DeskPricingRepositoryImpl
import repository.fragments.desk.DeskPricingRepoFragments.*
import shared.TransactorResource
import testData.TestConstants.*
import testData.DeskTestConstants.*
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class DeskPricingRepositoryISpec(global: GlobalRead) extends IOSuite with RepositoryISpecBase {

  type Res = DeskPricingRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskPricingsTable.update.run.transact(transactor.xa).void *>
        resetDeskPricingsTable.update.run.transact(transactor.xa).void *>
        insertDeskPricings.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, DeskPricingRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      deskPricingRepo = new DeskPricingRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield deskPricingRepo

    setup
  }

  test(".findByDeskId() - should return the desk pricing details for a given deskId") { deskPricingRepo =>

    val expectedPricing = sampleRetrievedDeskPricing

    for {
      deskPricingOpt <- deskPricingRepo.findByDeskId("deskId1")
    } yield expect(deskPricingOpt == Some(expectedPricing))
  }

  test(".findByOfficeId() - should return the all desk pricing for a given officeId") { deskPricingRepo =>

    val expectedPricing = sampleRetrievedDeskPricing

    for {
      deskPricingOpt <- deskPricingRepo.findByOfficeId("officeId1")
    } yield expect(deskPricingOpt == List(expectedPricing))
  }

  test(".update() - should return UpdateSuccess for successfuly updating a desk pricing") { deskPricingRepo =>

    val updateRequest =
      UpdateDeskPricingRequest(
        pricePerHour = 15.0,
        pricePerDay = Some(100.0),
        pricePerWeek = Some(600.0),
        pricePerMonth = Some(2000.0),
        pricePerYear = Some(24000.0)
      )

    for {
      deskPricingOpt <- deskPricingRepo.update("deskId1", updateRequest)
    } yield expect(deskPricingOpt == Valid(UpdateSuccess))
  }

  test(".delete() - should return DeleteSuccess for successfuly deleting the desk based on the deskId") { deskPricingRepo =>
    for {
      deskPricingOpt <- deskPricingRepo.delete(deskId2)
    } yield expect(deskPricingOpt == Valid(DeleteSuccess))
  }
}
