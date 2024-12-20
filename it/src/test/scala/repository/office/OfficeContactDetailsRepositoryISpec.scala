package repository.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.office.contact_details.OfficeContactDetails
import repositories.office.OfficeContactDetailsRepositoryImpl
import repository.fragments.OfficeContactDetailsRepoFragments.{createOfficeContactDetailsTable, resetOfficeContactDetailsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class OfficeContactDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void
    )

  def testOfficeContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails = {
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  private def seedTestContactDetails(officeContactDetailsRepo: OfficeContactDetailsRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testOfficeContactDetails(Some(1), "business_id_1", "office_1"),
      testOfficeContactDetails(Some(2), "business_id_2", "office_2"),
      testOfficeContactDetails(Some(3), "business_id_3", "office_3"),
      testOfficeContactDetails(Some(4), "business_id_4", "office_4"),
      testOfficeContactDetails(Some(5), "business_id_5", "office_5")
    )
    users.traverse(officeContactDetailsRepo.createContactDetails).void
  }

  def sharedResource: Resource[IO, OfficeContactDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeContactDetailsRepo = new OfficeContactDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestContactDetails(officeContactDetailsRepo))
    } yield officeContactDetailsRepo

    setup
  }

  test(".findByBusinessId() - should return the office ContactDetails if business_id exists for a previously created office ContactDetails") { officeContactDetailsRepo =>

    val officeContactDetails = testOfficeContactDetails(Some(1), "business_id_1", "office_1")

    val expectedResult =
      OfficeContactDetails(
        id = Some(1),
        businessId = "business_id_1",
        officeId = "office_1",
        primaryContactFirstName = "Michael",
        primaryContactLastName = "Yau",
        contactEmail = "mike@gmail.com",
        contactNumber = "07402205071",
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      officeContactDetailsOpt <- officeContactDetailsRepo.findByBusinessId("business_id_1")
      //      _ <- IO(println(s"Query Result: $officeContactDetailsOpt")) // Debug log the result
    } yield expect(officeContactDetailsOpt == Some(expectedResult))
  }
}
