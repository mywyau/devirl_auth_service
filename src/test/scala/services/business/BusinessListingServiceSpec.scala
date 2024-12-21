package services.business

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import models.business.address_details.BusinessAddress
import models.business.address_details.requests.BusinessAddressRequest
import models.business.adts.*
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import models.database.{SqlErrors, *}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessAddressRepositoryAlgebra, BusinessContactDetailsRepositoryAlgebra, BusinessSpecificationsRepositoryAlgebra}
import services.SpecBase
import services.business.business_listing.BusinessListingServiceImpl
import services.business.mocks.BusinessListingMocks.*
import services.constants.BusinessListingConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessListingServiceSpec extends SimpleIOSuite with SpecBase {

  def createTestService(
                         addressResult: IO[ValidatedNel[SqlErrors, Int]],
                         contactResult: IO[ValidatedNel[SqlErrors, Int]],
                         specsResult: IO[ValidatedNel[SqlErrors, Int]]
                       ): BusinessListingServiceImpl[IO] = {
    val addressRepo = new MockBusinessAddressRepository(addressResult)
    val contactRepo = new MockContactDetailsRepository(contactResult)
    val specsRepo = new MockSpecificationsRepository(specsResult)

    new BusinessListingServiceImpl[IO](addressRepo, contactRepo, specsRepo)
  }

  test(".createBusiness() - all repositories succeed") {

    val service =
      createTestService(
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
    val service =
      createTestService(
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
    val service =
      createTestService(
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
    val service =
      createTestService(
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
