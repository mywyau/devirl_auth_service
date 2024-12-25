package services.office.office_listing

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import models.database.{SqlErrors, *}
import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import models.office.contact_details.OfficeContactDetails
import models.office.office_listing.errors.OfficeListingErrors
import models.office.office_listing.requests.OfficeListingRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}
import repositories.office.{OfficeAddressRepositoryAlgebra, OfficeContactDetailsRepositoryAlgebra, OfficeSpecificationsRepositoryAlgebra}
import weaver.SimpleIOSuite

import java.time.{LocalDateTime, LocalTime}

object OfficeListingServiceSpec extends SimpleIOSuite {

  val testOfficeSpecs: OfficeSpecifications =
    OfficeSpecifications(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
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

  val testOfficeAddress =
    CreateOfficeAddressRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testOfficeContactDetails =
    OfficeContactDetails(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val officeListingRequest =
    OfficeListingRequest(
      officeId = "office_id_1",
      addressDetails = testOfficeAddress,
      officeSpecs = testOfficeSpecs,
      contactDetails = testOfficeContactDetails,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Mock repositories
  class MockOfficeAddressRepository(
                                     addressResult: IO[ValidatedNel[SqlErrors, Int]]
                                   ) extends OfficeAddressRepositoryAlgebra[IO] {

    override def findByOfficeId(officeId: String): IO[Option[OfficeAddress]] = ???

    override def create(officeAddressRequest: CreateOfficeAddressRequest): IO[ValidatedNel[SqlErrors, Int]] = addressResult

    override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }

  class MockContactDetailsRepository(
                                      contactResult: IO[ValidatedNel[SqlErrors, Int]]
                                    ) extends OfficeContactDetailsRepositoryAlgebra[IO] {

    override def findByOfficeId(businessId: String): IO[Option[OfficeContactDetails]] = ???

    override def create(officeContactDetails: OfficeContactDetails): IO[ValidatedNel[SqlErrors, Int]] = contactResult

    override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }

  class MockSpecsRepository(
                             specsResult: IO[ValidatedNel[SqlErrors, Int]]
                           ) extends OfficeSpecificationsRepositoryAlgebra[IO] {

    override def createSpecs(user: OfficeSpecifications): IO[ValidatedNel[SqlErrors, Int]] = specsResult

    override def findByOfficeId(officeId: String): IO[Option[OfficeSpecifications]] = ???

    override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }

  def createTestService(
                         addressResult: IO[ValidatedNel[SqlErrors, Int]],
                         contactResult: IO[ValidatedNel[SqlErrors, Int]],
                         specsResult: IO[ValidatedNel[SqlErrors, Int]]
                       ): OfficeListingServiceImpl[IO] = {
    val addressRepo = new MockOfficeAddressRepository(addressResult)
    val contactRepo = new MockContactDetailsRepository(contactResult)
    val specsRepo = new MockSpecsRepository(specsResult)

    new OfficeListingServiceImpl[IO](addressRepo, contactRepo, specsRepo)
  }

  test("createOffice - all repositories succeed") {
    val service = createTestService(
      IO.pure(Validated.valid(1)),
      IO.pure(Validated.valid(2)),
      IO.pure(Validated.valid(3))
    )

    val request = officeListingRequest

    for {
      result <- service.createOffice(request)
    } yield expect(result == Validated.valid(1))
  }

  test("createOffice - one repository fails") {
    val service = createTestService(
      IO.pure(Validated.valid(1)),
      IO.pure(Validated.invalidNel(ConstraintViolation)),
      IO.pure(Validated.valid(3))
    )

    val request = officeListingRequest

    for {
      result <- service.createOffice(request)
    } yield expect(result.isInvalid && result == Validated.invalidNel(DatabaseError))
  }

  test("createOffice - multiple repositories fail") {
    val service = createTestService(
      IO.pure(Validated.invalidNel(ConstraintViolation)),
      IO.pure(Validated.invalidNel(ConstraintViolation)),
      IO.pure(Validated.valid(3))
    )

    val request = officeListingRequest

    for {
      result <- service.createOffice(request)
    } yield {
      expect(result.isInvalid && result == Validated.invalidNel(DatabaseError))
    }
  }

  test("createOffice - unexpected exception during repository operation") {
    val service = createTestService(
      IO.raiseError(new RuntimeException("Unexpected database error")),
      IO.pure(Validated.valid(2)),
      IO.pure(Validated.valid(3))
    )

    val request = officeListingRequest

    for {
      result <- service.createOffice(request)
    } yield expect(result.isInvalid && result == Validated.invalidNel(UnknownError))
  }
}
