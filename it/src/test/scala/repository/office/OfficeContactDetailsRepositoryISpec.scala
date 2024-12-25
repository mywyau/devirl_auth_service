package repository.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import repositories.office.OfficeContactDetailsRepositoryImpl
import repository.fragments.OfficeContactDetailsRepoFragments.{createOfficeContactDetailsTable, insertOfficeContactDetailsData, resetOfficeContactDetailsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class OfficeContactDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        insertOfficeContactDetailsData.update.run.transact(transactor.xa).void
    )

  def testCreateOfficeContactDetailsRequest(businessId: String, officeId: String): CreateOfficeContactDetailsRequest = {
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )
  }

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
      OfficeContactDetails(
        id = Some(1),
        businessId = "BUS12345",
        officeId = "OFF001",
        primaryContactFirstName = "Alice",
        primaryContactLastName = "Johnson",
        contactEmail = "alice.johnson@example.com",
        contactNumber = "+15551234567",
        createdAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
        updatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
      )

    for {
      officeContactDetailsOpt <- officeContactDetailsRepo.findByOfficeId("OFF001")
      //      _ <- IO(println(s"Query Result: $officeContactDetailsOpt")) // Debug log the result
    } yield expect(officeContactDetailsOpt == Some(expectedResult))
  }
}
