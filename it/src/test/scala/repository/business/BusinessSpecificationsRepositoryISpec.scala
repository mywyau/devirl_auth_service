package repository.business

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessAvailability
import repositories.business.BusinessSpecificationsRepositoryImpl
import repository.fragments.business.BusinessSpecsRepoFragments.{createBusinessSpecsTable, resetBusinessSpecsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class BusinessSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessSpecsTable.update.run.transact(transactor.xa).void *>
        resetBusinessSpecsTable.update.run.transact(transactor.xa).void
    )

  def testBusinessSpecs(id: Option[Int], userId: String, businessId: String): BusinessSpecifications = {
    BusinessSpecifications(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = "build_123",
      description = "some description about the business",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  private def seedTestSpecs(businessSpecsRepo: BusinessSpecificationsRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testBusinessSpecs(Some(1), "user_id_1", "business_id_1"),
      testBusinessSpecs(Some(2), "user_id_2", "business_id_2"),
      testBusinessSpecs(Some(3), "user_id_3", "business_id_3"),
      testBusinessSpecs(Some(4), "user_id_4", "business_id_4"),
      testBusinessSpecs(Some(5), "user_id_5", "business_id_5")
    )
    users.traverse(businessSpecsRepo.createSpecs).void
  }

  def sharedResource: Resource[IO, BusinessSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessSpecsRepo = new BusinessSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestSpecs(businessSpecsRepo))
    } yield businessSpecsRepo

    setup
  }

  test(".findByBusinessId() - should return the business specifications if business_id exists for a previously created business specifications") { businessSpecsRepo =>

    val expectedResult =
      BusinessSpecifications(
        id = Some(1),
        userId = "user_id_1",
        businessId = "business_id_1",
        businessName = "build_123",
        description = "some description about the business",
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessSpecsOpt <- businessSpecsRepo.findByBusinessId("business_id_1")
    } yield expect(businessSpecsOpt == Some(expectedResult))
  }
}
