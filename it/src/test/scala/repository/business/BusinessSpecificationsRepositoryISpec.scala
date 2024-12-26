package repository.business

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import repositories.business.BusinessSpecificationsRepositoryImpl
import repository.fragments.business.BusinessSpecificationsRepoFragments.{createBusinessSpecsTable, insertBusinessSpecificationsData, resetBusinessSpecsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

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

    val expectedResult =
      BusinessSpecifications(
        id = Some(1),
        userId = "USER001",
        businessId = "BUS001",
        businessName = "business_name_1",
        description = "some desc1",
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessSpecsOpt <- businessSpecsRepo.findByBusinessId("BUS001")
    } yield expect(businessSpecsOpt == Some(expectedResult))
  }
}
