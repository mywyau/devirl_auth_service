package repository.business

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.business.contact_details.BusinessContactDetails
import repositories.business.BusinessContactDetailsRepositoryImpl
import repository.fragments.business.BusinessContactDetailsRepoFragments.{createBusinessContactDetailsTable, insertBusinessContactDetailsData, resetBusinessContactDetailsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class BusinessContactDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
        insertBusinessContactDetailsData.update.run.transact(transactor.xa).void
    )


  def sharedResource: Resource[IO, BusinessContactDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessContactDetailsRepo = new BusinessContactDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessContactDetailsRepo

    setup
  }

  test(".findByBusinessId() - should return the business contact details if business_id exists for a previously created business contact details") { businessContactDetailsRepo =>

    val businessId = "BUS001"

    val expectedResult =
      BusinessContactDetails(
        id = Some(1),
        userId = "USER001",
        businessId = businessId,
        businessName = Some("business_name_1"),
        primaryContactFirstName = Some("Bob1"),
        primaryContactLastName = Some("Smith"),
        contactEmail = Some("bob1@gmail.com"),
        contactNumber = Some("07402205071"),
        websiteUrl = Some("bobs_axes.com"),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessContactDetailsOpt <- businessContactDetailsRepo.findByBusinessId(businessId)
    } yield expect(businessContactDetailsOpt == Some(expectedResult))
  }
}
