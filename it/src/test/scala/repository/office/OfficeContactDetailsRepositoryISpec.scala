package repository.office

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import models.desk.deskSpecifications.PrivateDesk
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import repositories.office.OfficeContactDetailsRepositoryImpl
import repository.fragments.OfficeContactDetailsRepoFragments.createOfficeContactDetailsTable
import repository.fragments.OfficeContactDetailsRepoFragments.insertOfficeContactDetailsData
import repository.fragments.OfficeContactDetailsRepoFragments.resetOfficeContactDetailsTable
import testData.TestConstants.*
import testData.OfficeTestConstants.*
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class OfficeContactDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        insertOfficeContactDetailsData.update.run.transact(transactor.xa).void
    )

  def testCreateOfficeContactDetailsRequest(businessId: String, officeId: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  def sharedResource: Resource[IO, OfficeContactDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeContactDetailsRepo = new OfficeContactDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield officeContactDetailsRepo

    setup
  }

  test(".findByOfficeId() - should return the office ContactDetails if office_id exists for a previously created office ContactDetails") { officeContactDetailsRepo =>

    val expectedResult =
      OfficeContactDetailsPartial(
        businessId = "BUS12345",
        officeId = "OFF001",
        primaryContactFirstName = Some("Alice"),
        primaryContactLastName = Some("Johnson"),
        contactEmail = Some("alice.johnson@example.com"),
        contactNumber = Some("+15551234567")
      )

    for {
      officeContactDetailsOpt <- officeContactDetailsRepo.findByOfficeId("OFF001")
    } yield expect(officeContactDetailsOpt == Some(expectedResult))
  }
}
