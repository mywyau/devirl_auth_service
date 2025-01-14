package controllers.constants

import cats.effect.*
import models.business.address.requests.CreateBusinessAddressRequest
import models.business_listing.requests.BusinessListingRequest
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.specifications.BusinessAvailability
import models.business.specifications.requests.CreateBusinessSpecificationsRequest

import java.time.LocalDateTime
import java.time.LocalTime

object BusinessListingControllerConstants {

  val testCreateBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "Modern Workspace",
      description = "A vibrant business space in the heart of the city, ideal for teams or individuals.",
      availability = BusinessAvailability(
        days = List("Monday", "Tuesday"),
        startTime = LocalTime.of(10, 0, 0),
        endTime = LocalTime.of(10, 30, 0)
      )
    )

  val testCreateBusinessAddressRequest: CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = Some("MikeyCorp"),
      buildingName = Some("BusinessListingControllerISpec Building"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "businessCorp",
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
