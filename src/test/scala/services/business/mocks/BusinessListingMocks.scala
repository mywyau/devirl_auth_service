package services.business.mocks

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import models.business.address.BusinessAddress
import models.business.address.requests.BusinessAddressRequest
import models.business.adts.*
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import models.database.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessAddressRepositoryAlgebra, BusinessContactDetailsRepositoryAlgebra, BusinessSpecificationsRepositoryAlgebra}
import services.business.business_listing.BusinessListingServiceImpl
import weaver.SimpleIOSuite

object BusinessListingMocks {

  class MockBusinessAddressRepository(
                                       addressResult: IO[ValidatedNel[SqlErrors, Int]]
                                     ) extends BusinessAddressRepositoryAlgebra[IO] {

    override def findByBusinessId(userId: String): IO[Option[BusinessAddress]] = ???

    override def createBusinessAddress(businessAddress: BusinessAddressRequest): IO[ValidatedNel[SqlErrors, Int]] = addressResult

    override def deleteBusinessAddress(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }

  class MockContactDetailsRepository(
                                      contactResult: IO[ValidatedNel[SqlErrors, Int]]
                                    ) extends BusinessContactDetailsRepositoryAlgebra[IO] {

    override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] = ???

    override def createContactDetails(businessContactDetails: BusinessContactDetails): IO[ValidatedNel[SqlErrors, Int]] = contactResult

    override def deleteContactDetails(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }

  class MockSpecificationsRepository(
                                      specsResult: IO[ValidatedNel[SqlErrors, Int]]
                                    ) extends BusinessSpecificationsRepositoryAlgebra[IO] {

    override def findByBusinessId(businessId: String): IO[Option[BusinessSpecifications]] = ???

    override def createSpecs(user: BusinessSpecifications): IO[ValidatedNel[SqlErrors, Int]] = specsResult

    override def deleteSpecifications(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }
}
