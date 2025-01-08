package services.business

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import models.business.address.BusinessAddress
import models.business.adts.*
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import models.database.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessAddressRepositoryAlgebra, BusinessContactDetailsRepositoryAlgebra, BusinessSpecificationsRepositoryAlgebra}
import services.SpecBase
import services.business.business_listing.BusinessListingServiceImpl
import services.business.mocks.BusinessListingMocks.*
import services.constants.BusinessListingServiceConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessListingServiceSpec extends SimpleIOSuite with SpecBase {

  def createTestService(): BusinessListingServiceImpl[IO] = {
  
    val listingRepo = new MockBusinessListingRepository()

    new BusinessListingServiceImpl[IO](listingRepo)
  }


}
