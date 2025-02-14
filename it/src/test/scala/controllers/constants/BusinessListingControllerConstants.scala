package controllers.constants

import cats.effect.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.business_listing.requests.BusinessListingRequest
import testData.BusinessTestConstants.*
import testData.TestConstants.*

object BusinessListingControllerConstants {

  val testCreateBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = "userId1",
      businessId = "businessId1",
      businessName = "Maginificanent Office",
      description = "A vibrant business space in the heart of the city, ideal for teams or individuals.",
      openingHours = businessOpeningHours1
    )

  val testCreateBusinessAddressRequest: CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = "userId1",
      businessId = "businessId1",
      businessName = Some("MikeyCorp"),
      buildingName = Some("BusinessListingControllerISpec Building"),
      floorNumber = Some("floor 1"),
      street = Some("Main street 123"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("County 123"),
      postcode = Some("123456"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = "userId1",
      businessId = "businessId1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com"
    )

  def testBusinessListingRequest(businessId: String): BusinessListingRequest =
    BusinessListingRequest(
      businessId = businessId,
      addressDetails = testCreateBusinessAddressRequest,
      businessSpecs = testCreateBusinessSpecificationsRequest,
      contactDetails = testCreateBusinessContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
