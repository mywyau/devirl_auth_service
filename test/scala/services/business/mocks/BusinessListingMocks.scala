package services.business.mocks

import cats.data.NonEmptyList
import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.address.BusinessAddressPartial
import models.business.business_listing.requests.InitiateBusinessListingRequest
import models.business.business_listing.BusinessListing
import models.business.contact_details.BusinessContactDetailsPartial
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecificationsPartial
import models.database.DatabaseError
import models.database.DatabaseErrors
import repositories.business.BusinessListingRepositoryAlgebra
import models.database.DatabaseSuccess
import models.database.CreateSuccess

object BusinessListingMocks {

  val mockBusinessAddressPartial: BusinessAddressPartial =
    BusinessAddressPartial(
      userId = "user-123",
      businessId = "business-001",
      buildingName = Some("Test Building"),
      floorNumber = Some("5"),
      street = Some("123 Test Street"),
      city = Some("Test City"),
      country = Some("Test Country"),
      county = Some("Test County"),
      postcode = Some("12345"),
      latitude = Some(BigDecimal(51.5074)),
      longitude = Some(BigDecimal(-0.1278))
    )

  val mockBusinessContactDetailsPartial: BusinessContactDetailsPartial =
    BusinessContactDetailsPartial(
      userId = "user-123",
      businessId = "business-001",
      primaryContactFirstName = Some("John"),
      primaryContactLastName = Some("Doe"),
      contactEmail = Some("contact@testbusiness.com"),
      contactNumber = Some("+1234567890"),
      websiteUrl = Some("https://testbusiness.com")
    )

  val mockBusinessAvailability: BusinessAvailability =
    BusinessAvailability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(9, 0, 0),
      endTime = LocalTime.of(17, 0, 0)
    )

  val mockBusinessSpecificationsPartial: BusinessSpecificationsPartial =
    BusinessSpecificationsPartial(
      userId = "user-123",
      businessId = "business-001",
      businessName = Some("Test Business"),
      description = Some("A test business providing exemplary services."),
      availability = Some(mockBusinessAvailability)
    )

  val mockBusinessListings: List[BusinessListing] = List(
    BusinessListing(
      userId = "user-001",
      businessId = "business-001",
      addressDetails = mockBusinessAddressPartial,
      contactDetails = mockBusinessContactDetailsPartial,
      specifications = mockBusinessSpecificationsPartial
    ),
    BusinessListing(
      userId = "user-002",
      businessId = "business-002",
      addressDetails = mockBusinessAddressPartial.copy(businessId = "business-002"),
      contactDetails = mockBusinessContactDetailsPartial.copy(businessId = "business-002", contactEmail = Some("contact2@testbusiness.com")),
      specifications = mockBusinessSpecificationsPartial.copy(businessId = "business-002", description = Some("Another business."))
    )
  )

  val mockSqlSuccess: ValidatedNel[DatabaseErrors, DatabaseSuccess] = Validated.Valid(CreateSuccess)
  val mockSqlError: ValidatedNel[DatabaseErrors, DatabaseSuccess] = Validated.Invalid(NonEmptyList.one(DatabaseError))

  // Mock repository implementation
  class MockBusinessListingRepository() extends BusinessListingRepositoryAlgebra[IO] {

    override def findAll(): IO[List[BusinessListing]] = IO.pure(mockBusinessListings)

    override def findByBusinessId(businessId: String): IO[Option[BusinessListing]] =
      IO.pure(mockBusinessListings.find(_.businessId == businessId))

    override def initiate(request: InitiateBusinessListingRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
      IO.pure(mockSqlSuccess)

    override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
      if (mockBusinessListings.exists(_.businessId == businessId)) IO.pure(mockSqlSuccess)
      else IO.pure(mockSqlError)

    override def deleteByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
      if (userId == "user-123") IO.pure(mockSqlSuccess)
      else IO.pure(mockSqlError)
  }
}
