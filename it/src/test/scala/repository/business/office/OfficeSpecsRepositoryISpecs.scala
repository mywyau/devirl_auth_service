package repository.business.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.office.office_specs.OfficeSpecs
import models.office.adts.OpenPlanOffice
import models.office.office_specs.OfficeAvailability
import repositories.office.OfficeSpecsRepositoryImpl
import repository.fragments.OfficeSpecsRepoFragments.{createOfficeSpecsTable, resetOfficeSpecsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class OfficeSpecsRepositoryISpecs(global: GlobalRead) extends IOSuite {

  type Res = OfficeSpecsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        resetOfficeSpecsTable.update.run.transact(transactor.xa).void
    )

  def testOfficeSpecs(id: Option[Int], businessId: String, officeId: String): OfficeSpecs = {
    OfficeSpecs(
      id = Some(1),
      businessId = businessId,
      officeId = officeId,
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability =
        OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          startTime = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
          endTime = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
        ),
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  private def seedTestSpecs(officeSpecsRepo: OfficeSpecsRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testOfficeSpecs(Some(1), "business_id_1", "office_1"),
      testOfficeSpecs(Some(2), "business_id_2", "office_2"),
      testOfficeSpecs(Some(3), "business_id_3", "office_3"),
      testOfficeSpecs(Some(4), "business_id_4", "office_4"),
      testOfficeSpecs(Some(5), "business_id_5", "office_5")
    )
    users.traverse(officeSpecsRepo.createSpecs).void
  }

  def sharedResource: Resource[IO, OfficeSpecsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeSpecsRepo = new OfficeSpecsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestSpecs(officeSpecsRepo))
    } yield officeSpecsRepo

    setup
  }

  test(".findByBusinessId() - should return the office address if business_id exists for a previously created office address") { officeSpecsRepo =>

    val officeSpecs = testOfficeSpecs(Some(1), "business_id_1", "office_1")

    val expectedResult =
      OfficeSpecs(
        id = Some(1),
        businessId = "business_id_1",
        officeId = "office_1",
        officeName = "Modern Workspace",
        description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
        officeType = OpenPlanOffice,
        numberOfFloors = 3,
        totalDesks = 3,
        capacity = 50,
        amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
        availability =
          OfficeAvailability(
            days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
            startTime = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
            endTime = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
          ),
        rules = Some("No smoking. Maintain cleanliness."),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      officeSpecsOpt <- officeSpecsRepo.findByBusinessId("business_id_1")
      //      _ <- IO(println(s"Query Result: $officeSpecsOpt")) // Debug log the result
    } yield expect(officeSpecsOpt == Some(expectedResult))
  }
}
