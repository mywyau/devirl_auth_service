package services.business

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import models.business.adts.*
import models.business.business_address.service.BusinessAddress
import models.business.business_address.service.BusinessAddress
import models.business.business_contact_details.BusinessContactDetails
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.business_specs.{BusinessAvailability, BusinessSpecs}
import models.database.{SqlErrors, *}
import repositories.business.{BusinessAddressRepositoryAlgebra, BusinessContactDetailsRepositoryAlgebra, BusinessSpecsRepositoryAlgebra}
import services.business.business_listing.BusinessListingServiceImpl
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessListingServiceSpec extends SimpleIOSuite {

  val testBusinessSpecs: BusinessSpecs =
    BusinessSpecs(
      id = Some(1),
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "Modern Workspace",
      description = "A vibrant business space in the heart of the city, ideal for teams or individuals.",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testBusinessAddress: BusinessAddress =
    BusinessAddress(
      id = Some(1),
      userId = "user_id_1",
      businessId = Some("business_id_1"),
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testBusinessContactDetails =
    BusinessContactDetails(
      id = Some(1),
      userId = "user_id_1",
      businessId = "business_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val businessListingRequest: BusinessListingRequest =
    BusinessListingRequest(
      businessId = "business_id_1",
      addressDetails = testBusinessAddress,
      businessSpecs = testBusinessSpecs,
      contactDetails = testBusinessContactDetails,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Mock repositories
  class MockBusinessAddressRepository(
                                       addressResult: IO[ValidatedNel[SqlErrors, Int]]
                                     ) extends BusinessAddressRepositoryAlgebra[IO] {

    override def findByUserId(userId: String): IO[Option[BusinessAddress]] = ???

    override def createBusinessAddress(businessAddress: BusinessAddress): IO[ValidatedNel[SqlErrors, Int]] = addressResult
  }

  class MockContactDetailsRepository(
                                      contactResult: IO[ValidatedNel[SqlErrors, Int]]
                                    ) extends BusinessContactDetailsRepositoryAlgebra[IO] {

    override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] = ???

    override def createContactDetails(businessContactDetails: BusinessContactDetails): IO[ValidatedNel[SqlErrors, Int]] = contactResult
  }

  class MockSpecsRepository(
                             specsResult: IO[ValidatedNel[SqlErrors, Int]]
                           ) extends BusinessSpecsRepositoryAlgebra[IO] {

    override def findByBusinessId(businessId: String): IO[Option[BusinessSpecs]] = ???

    override def createSpecs(user: BusinessSpecs): IO[ValidatedNel[SqlErrors, Int]] = specsResult
  }

  def createTestService(
                         addressResult: IO[ValidatedNel[SqlErrors, Int]],
                         contactResult: IO[ValidatedNel[SqlErrors, Int]],
                         specsResult: IO[ValidatedNel[SqlErrors, Int]]
                       ): BusinessListingServiceImpl[IO] = {
    val addressRepo = new MockBusinessAddressRepository(addressResult)
    val contactRepo = new MockContactDetailsRepository(contactResult)
    val specsRepo = new MockSpecsRepository(specsResult)

    new BusinessListingServiceImpl[IO](addressRepo, contactRepo, specsRepo)
  }

  test(".createBusiness() - all repositories succeed") {
    val service = createTestService(
      IO.pure(Validated.valid(1)),
      IO.pure(Validated.valid(2)),
      IO.pure(Validated.valid(3))
    )

    val request = businessListingRequest

    for {
      result <- service.createBusiness(request)
    } yield expect(result == Validated.valid(1))
  }

  test(".createBusiness() - one repository fails") {
    val service = createTestService(
      IO.pure(Validated.valid(1)),
      IO.pure(Validated.invalidNel(ConstraintViolation)),
      IO.pure(Validated.valid(3))
    )

    val request = businessListingRequest

    for {
      result <- service.createBusiness(request)
    } yield expect(result.isInvalid && result == Validated.invalidNel(DatabaseError))
  }

  test(".createBusiness() - multiple repositories fail") {
    val service = createTestService(
      IO.pure(Validated.invalidNel(ConstraintViolation)),
      IO.pure(Validated.invalidNel(ConstraintViolation)),
      IO.pure(Validated.valid(3))
    )

    val request = businessListingRequest

    for {
      result <- service.createBusiness(request)
    } yield {
      expect(result.isInvalid && result == Validated.invalidNel(DatabaseError))
    }
  }

  test(".createBusiness() - unexpected exception during repository operation") {
    val service = createTestService(
      IO.raiseError(new RuntimeException("Unexpected database error")),
      IO.pure(Validated.valid(2)),
      IO.pure(Validated.valid(3))
    )

    val request = businessListingRequest

    for {
      result <- service.createBusiness(request)
    } yield expect(result.isInvalid && result == Validated.invalidNel(UnknownError))
  }
}
