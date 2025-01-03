package services.business.mocks

import java.time.LocalDateTime
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import models.business.address.BusinessAddress
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.BusinessAvailability
import models.business.business_listing.BusinessListing
import models.database.DatabaseErrors
import models.database.DatabaseError
import repositories.business.BusinessListingRepositoryAlgebra
import java.time.LocalTime
import models.business.specifications.BusinessSpecifications
import models.business.business_listing.requests.InitiateBusinessListingRequest


object BusinessListingMocks {

  val mockBusinessAddress: BusinessAddress = BusinessAddress(
    id = Some(1),
    userId = "user-123",
    businessId = "business-001",
    businessName = Some("Test Business"),
    buildingName = Some("Test Building"),
    floorNumber = Some("5"),
    street = Some("123 Test Street"),
    city = Some("Test City"),
    country = Some("Test Country"),
    county = Some("Test County"),
    postcode = Some("12345"),
    latitude = Some(BigDecimal(51.5074)),
    longitude = Some(BigDecimal(-0.1278)),
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now()
  )

  val mockBusinessContactDetails: BusinessContactDetails = BusinessContactDetails(
    id = Some(1),
    userId = "user-123",
    businessId = "business-001",
    businessName = Some("Test Business"),
    primaryContactFirstName = Some("John"),
    primaryContactLastName = Some("Doe"),
    contactEmail = Some("contact@testbusiness.com"),
    contactNumber = Some("+1234567890"),
    websiteUrl = Some("https://testbusiness.com"),
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now()
  )

  val mockBusinessAvailability: BusinessAvailability =
     BusinessAvailability(
    days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
    startTime = LocalTime.of(9,0,0),
    endTime = LocalTime.of(17,0,0)
  )

  val mockBusinessSpecifications: BusinessSpecifications = BusinessSpecifications(
    id = Some(1),
    userId = "user-123",
    businessId = "business-001",
    businessName = Some("Test Business"),
    description = Some("A test business providing exemplary services."),
    availability = Some(mockBusinessAvailability),
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now()
  )

  val mockBusinessListings: List[BusinessListing] = List(
    BusinessListing(
      businessId = "business-001",
      addressDetails = mockBusinessAddress,
      businessContactDetails = mockBusinessContactDetails,
      businessSpecs = mockBusinessSpecifications
    ),
    BusinessListing(
      businessId = "business-002",
      addressDetails = mockBusinessAddress.copy(businessId = "business-002", businessName = Some("Second Business")),
      businessContactDetails = mockBusinessContactDetails.copy(businessId = "business-002", contactEmail = Some("contact2@testbusiness.com")),
      businessSpecs = mockBusinessSpecifications.copy(businessId = "business-002", description = Some("Another business."))
    )
  )

  val mockSqlSuccess: ValidatedNel[DatabaseErrors, Int] = Validated.Valid(1)
  val mockSqlError: ValidatedNel[DatabaseErrors, Int] = Validated.Invalid(NonEmptyList.one(DatabaseError))

  // Mock repository implementation
  class MockBusinessListingRepository() extends BusinessListingRepositoryAlgebra[IO] {

    override def findAll(): IO[List[BusinessListing]] = IO.pure(mockBusinessListings)

    override def findByBusinessId(businessId: String): IO[Option[BusinessListing]] =
      IO.pure(mockBusinessListings.find(_.businessId == businessId))

    override def initiate(request: InitiateBusinessListingRequest): IO[ValidatedNel[DatabaseErrors, Int]] =
      IO.pure(mockSqlSuccess)

    override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] =
      if (mockBusinessListings.exists(_.businessId == businessId)) IO.pure(mockSqlSuccess)
      else IO.pure(mockSqlError)

    override def deleteByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, Int]] =
      if (userId == "user-123") IO.pure(mockSqlSuccess)
      else IO.pure(mockSqlError)
  }
}
