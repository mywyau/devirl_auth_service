package controllers.constants

import cats.effect.*
import models.business.adts.*
import models.business.business_address.requests.BusinessAddressRequest
import models.business.business_address.service.BusinessAddress
import models.business.business_contact_details.BusinessContactDetails
import models.business.business_listing.requests.BusinessListingRequest
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}

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

  val testBusinessAddress: BusinessAddress =
    BusinessAddress(
      id = Some(1),
      userId = "user_id_1",
      businessId = Some("business_id_1"),
      businessName = Some("MikeyCorp"),
      buildingName = Some("BusinessListingControllerISpec Building"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testBusinessAddressRequest: BusinessAddressRequest =
    BusinessAddressRequest(
      userId = "user_id_1",
      businessId = Some("business_id_1"),
      businessName = Some("MikeyCorp"),
      buildingName = Some("BusinessListingControllerISpec Building"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


  val testBusinessContactDetails: BusinessContactDetails =
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


  def testBusinessListingRequest(businessId: String): BusinessListingRequest =
    BusinessListingRequest(
      businessId = businessId,
      addressDetails = testBusinessAddressRequest,
      businessSpecs = testBusinessSpecs,
      contactDetails = testBusinessContactDetails,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
