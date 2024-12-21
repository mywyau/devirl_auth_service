package services.constants

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits.*
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
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

import java.time.LocalDateTime

object BusinessContactDetailsConstants {

  def testContactDetails(id: Option[Int], userId: String, businessId: String, business_id: String): BusinessContactDetails =
    BusinessContactDetails(
      id = Some(1),
      userId = userId,
      businessId = businessId,
      businessName = "MikeyCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


}
