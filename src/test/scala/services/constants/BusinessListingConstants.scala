package services.constants

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
import models.database.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessAddressRepositoryAlgebra, BusinessContactDetailsRepositoryAlgebra, BusinessSpecificationsRepositoryAlgebra}
import services.business.business_listing.BusinessListingServiceImpl
import weaver.SimpleIOSuite

import java.time.LocalDateTime


object BusinessListingConstants {

  val testBusinessSpecs: BusinessSpecifications =
    BusinessSpecifications(
      id = Some(1),
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "Modern Workspace",
      description = "A vibrant business space in the heart of the city, ideal for teams or individuals.",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testBusinessAddressRequest: BusinessAddressRequest =
    BusinessAddressRequest(
      userId = "user_id_1",
      businessId = Some("business_id_1"),
      businessName = Some("businessCorp"),
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
      businessName = "businessCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val businessListingRequest: BusinessListingRequest =
    BusinessListingRequest(
      businessId = "business_id_1",
      addressDetails = testBusinessAddressRequest,
      businessSpecs = testBusinessSpecs,
      contactDetails = testBusinessContactDetails,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


}
