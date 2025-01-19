package repository.business

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import models.desk.deskSpecifications.PrivateDesk
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial
import repositories.business.BusinessContactDetailsRepositoryImpl
import repository.fragments.business.BusinessContactDetailsRepoFragments.createBusinessContactDetailsTable
import repository.fragments.business.BusinessContactDetailsRepoFragments.insertBusinessContactDetailsData
import repository.fragments.business.BusinessContactDetailsRepoFragments.resetBusinessContactDetailsTable
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

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
      BusinessContactDetailsPartial(
        userId = "USER001",
        businessId = businessId,
        primaryContactFirstName = Some("Bob1"),
        primaryContactLastName = Some("Smith"),
        contactEmail = Some("bob1@gmail.com"),
        contactNumber = Some("07402205071"),
        websiteUrl = Some("bobs_axes.com")
      )

    for {
      businessContactDetailsOpt <- businessContactDetailsRepo.findByBusinessId(businessId)
    } yield expect(businessContactDetailsOpt == Some(expectedResult))
  }
}
