package services.office.office_listing

import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.IO
import cats.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.database.*
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddressPartial
import models.office.adts.*
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.office.office_listing.requests.OfficeListingRequest
import models.office.office_listing.OfficeListing
import models.office.office_listing.OfficeListingCard
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import repositories.office.OfficeListingRepositoryAlgebra
import weaver.SimpleIOSuite

object OfficeListingServiceSpec extends SimpleIOSuite {

  val testCreateOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability = OfficeAvailability(
        days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
        startTime = LocalTime.of(10, 0, 0),
        endTime = LocalTime.of(10, 30, 0)
      ),
      rules = Some("No smoking. Maintain cleanliness.")
    )

  val testCreateOfficeAddressRequest =
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
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mike@gmail.com"),
      contactNumber = Some("07402205071"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testCreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  val officeListingRequest =
    OfficeListingRequest(
      officeId = "office_id_1",
      createOfficeAddressRequest = testCreateOfficeAddressRequest,
      createOfficeSpecificationsRequest = testCreateOfficeSpecificationsRequest,
      createOfficeContactDetailsRequest = testCreateOfficeContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def testOfficeSpecificationsPartial(businessId: String, officeId: String): OfficeSpecificationsPartial =
    OfficeSpecificationsPartial(
      businessId = businessId,
      officeId = officeId,
      officeName = Some("some office"),
      description = Some("some desc"),
      officeType = None,
      numberOfFloors = None,
      totalDesks = None,
      capacity = None,
      amenities = None,
      availability = None,
      rules = None
    )

  def testOfficeAddressPartial(businessId: String, officeId: String): OfficeAddressPartial =
    OfficeAddressPartial(
      businessId = businessId,
      officeId = officeId,
      buildingName = None,
      floorNumber = None,
      street = None,
      city = None,
      country = None,
      county = None,
      postcode = None,
      latitude = None,
      longitude = None
    )

  def testContactDetailsPartial(businessId: String, officeId: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = None,
      primaryContactLastName = None,
      contactEmail = None,
      contactNumber = None
    )

  def testOfficeListing(businessId: String, officeId: String): OfficeListing =
    OfficeListing(
      officeId = officeId,
      addressDetails = testOfficeAddressPartial(businessId, officeId),
      contactDetails = testContactDetailsPartial(businessId, officeId),
      specifications = testOfficeSpecificationsPartial(businessId, officeId)
    )

  def testInitiateOfficeListingRequest(businessId: String, officeId: String): InitiateOfficeListingRequest =
    InitiateOfficeListingRequest(
      businessId = businessId,
      officeId = officeId,
      officeName = "some office",
      description = "some desc"
    )

  class MockOfficeListingRepository(
    findByOfficeIdResult: IO[Option[OfficeListing]],
    listingResult: IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
  ) extends OfficeListingRepositoryAlgebra[IO] {

    override def findAll(businessId: String): IO[List[OfficeListing]] = ???

    override def findByOfficeId(officeId: String): IO[Option[OfficeListing]] = findByOfficeIdResult

    override def initiate(request: InitiateOfficeListingRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = listingResult

    override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

    override def deleteByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  }

  def createTestService(
    findByOfficeIdResult: IO[Option[OfficeListing]],
    listingResult: IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
  ): OfficeListingServiceImpl[IO] = {

    val listingRepo = new MockOfficeListingRepository(findByOfficeIdResult, listingResult)
    new OfficeListingServiceImpl[IO](listingRepo)
  }

  test("initiate - all repositories succeed") {
    val service = createTestService(
      IO.pure(
        Some(testOfficeListing("business_id_1", "office_id_1"))
      ),
      IO.pure(Validated.valid(CreateSuccess))
    )

    val request = testInitiateOfficeListingRequest("business_id_1", "office_id_1")

    val expectedResult =
      OfficeListingCard(
        businessId = "business_id_1",
        officeId = "office_id_1",
        officeName = "some office",
        description = "some desc"
      )

    for {
      result <- service.initiate(request)
    } yield expect(result == Some(expectedResult))
  }

  test("initiate - one repository fails") {
    val service =
      createTestService(
        IO.pure(None),
        IO.pure(Validated.invalidNel(ConstraintViolation))
      )

    val request = testInitiateOfficeListingRequest("business_id_1", "office_id_1")
    val expectedResult = testOfficeListing("business_id_1", "office_id_1")

    for {
      result <- service.initiate(request)
    } yield expect(result == None)
  }
}
