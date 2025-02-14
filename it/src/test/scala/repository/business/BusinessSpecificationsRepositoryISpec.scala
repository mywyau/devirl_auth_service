package repository.business

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import repositories.business.BusinessSpecificationsRepositoryImpl
import repository.fragments.business.BusinessSpecificationsRepoFragments.*
import shared.TransactorResource
import testData.BusinessTestConstants.*
import testData.TestConstants.*
import utils.Diffable
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class BusinessSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessSpecsTable.update.run.transact(transactor.xa).void *>
        resetBusinessSpecsTable.update.run.transact(transactor.xa).void *>
        insertBusinessSpecificationsData.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, BusinessSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessSpecsRepo = new BusinessSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessSpecsRepo

    setup
  }

  test(".findByBusinessId() - should return the business specifications if business_id exists for a previously created business specifications") { businessSpecsRepo =>

    val userId = "userId1"
    val businessId = "businessId1"

    val expectedResult =
      BusinessSpecificationsPartial(
        userId = userId,
        businessId = businessId,
        businessName = Some("businessName1"),
        description = Some("some description"),
        openingHours = Some(businessOpeningHours1)
      )

    for {
      businessSpecsOpt <- businessSpecsRepo.findByBusinessId(businessId)
      _ = businessSpecsOpt.foreach(businessSpec => Diffable.logDifferences(expectedResult, businessSpec))
    } yield expect(businessSpecsOpt == Some(expectedResult))
  }
}
