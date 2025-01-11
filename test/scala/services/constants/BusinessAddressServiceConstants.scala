package services.constants

import cats.data.NonEmptyList
import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.IO
import cats.implicits.*
import java.time.LocalDateTime
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.BusinessAddressPartial
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecifications
import models.database.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.business.BusinessAddressRepositoryAlgebra
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import repositories.business.BusinessSpecificationsRepositoryAlgebra
import services.business.business_listing.BusinessListingServiceImpl
import weaver.SimpleIOSuite

object BusinessAddressServiceConstants {

  def testBusinessAddressRequest(userId: String, businessId: String): CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("mikeyCorp"),
      buildingName = Some("building name"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  def testBusinessAddress(userId: String, businessId: String): BusinessAddressPartial =
    BusinessAddressPartial(
      userId = userId,
      businessId = businessId,
      buildingName = Some("building name"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

}
