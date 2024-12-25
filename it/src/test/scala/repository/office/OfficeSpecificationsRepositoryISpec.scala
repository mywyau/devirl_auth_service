package repository.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.office.adts.{OpenPlanOffice, PrivateOffice}
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}
import repositories.office.OfficeSpecificationsRepositoryImpl
import repository.fragments.OfficeSpecificationsRepoFragments.{createOfficeSpecsTable, insertOfficeSpecificationData, resetOfficeSpecsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.{LocalDateTime, LocalTime}

class OfficeSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        resetOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        insertOfficeSpecificationData.update.run.transact(transactor.xa).void
    )

  def testCreateOfficeSpecificationsRequest(businessId: String, officeId: String): CreateOfficeSpecificationsRequest = {
    CreateOfficeSpecificationsRequest(
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
          startTime = LocalTime.of(10, 0, 0),
          endTime = LocalTime.of(10, 30, 0)
        ),
      rules = Some("No smoking. Maintain cleanliness.")
    )
  }


  def testOfficeSpecs(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications = {
    OfficeSpecifications(
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
          startTime = LocalTime.of(10, 0, 0),
          endTime = LocalTime.of(10, 30, 0)
        ),
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def sharedResource: Resource[IO, OfficeSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeSpecsRepo = new OfficeSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield officeSpecsRepo

    setup
  }

  test(
    ".findByOfficeId() - should return the office specifications if office_id exists for a previously created office specifications"
  ) {
    officeSpecificationsRepo =>

      val expectedResult =
        OfficeSpecifications(
          id = Some(1),
          businessId = "BUS001",
          officeId = "OFF001",
          officeName = "Main Office",
          description = "Spacious and well-lit office for teams.",
          officeType = PrivateOffice,
          numberOfFloors = 2,
          totalDesks = 20,
          capacity = 50,
          amenities = List("WiFi", "Parking"),
          availability =
            OfficeAvailability(
              days = List("Monday", "Friday"),
              startTime = LocalTime.of(9, 0, 0),
              endTime = LocalTime.of(17, 0, 0)
            ),
          rules = Some("No smoking indoors."),
          createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
        )

      for {
        officeSpecsOpt <- officeSpecificationsRepo.findByOfficeId("OFF001")
        //      _ <- IO(println(s"Query Result: $officeSpecsOpt")) // Debug log the result
      } yield expect(officeSpecsOpt == Some(expectedResult))
  }
}
