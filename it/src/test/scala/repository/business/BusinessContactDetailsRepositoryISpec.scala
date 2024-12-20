package repository.business

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.business.contact_details.BusinessContactDetails
import repositories.business.BusinessContactDetailsRepositoryImpl
import repository.fragments.business.BusinessContactDetailsRepoFragments.{createBusinessContactDetailsTable, resetBusinessContactDetailsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class BusinessContactDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetBusinessContactDetailsTable.update.run.transact(transactor.xa).void
    )

  def testBusinessContactDetails(id: Option[Int], userId: String, businessId: String): BusinessContactDetails = {
    BusinessContactDetails(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = "businessCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mikey@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  private def seedTestContactDetails(businessContactDetailsRepo: BusinessContactDetailsRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testBusinessContactDetails(Some(1), "user_id_1", "business_id_1"),
      testBusinessContactDetails(Some(2), "user_id_2", "business_id_2"),
      testBusinessContactDetails(Some(3), "user_id_3", "business_id_3"),
      testBusinessContactDetails(Some(4), "user_id_4", "business_id_4"),
      testBusinessContactDetails(Some(5), "user_id_5", "business_id_5")
    )
    users.traverse(businessContactDetailsRepo.createContactDetails).void
  }

  def sharedResource: Resource[IO, BusinessContactDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessContactDetailsRepo = new BusinessContactDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestContactDetails(businessContactDetailsRepo))
    } yield businessContactDetailsRepo

    setup
  }

  test(".findByBusinessId() - should return the business contact details if business_id exists for a previously created business contact details") { businessContactDetailsRepo =>

    val expectedResult =
      BusinessContactDetails(
        id = Some(1),
        userId = "user_id_1",
        businessId = "business_id_1",
        businessName = "businessCorp",
        primaryContactFirstName = "Michael",
        primaryContactLastName = "Yau",
        contactEmail = "mikey@gmail.com",
        contactNumber = "07402205071",
        websiteUrl = "mikey.com",
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessContactDetailsOpt <- businessContactDetailsRepo.findByBusinessId("business_id_1")
    } yield expect(businessContactDetailsOpt == Some(expectedResult))
  }
}
