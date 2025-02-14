package services.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.business_listing.requests.BusinessListingRequest
import models.Monday
import models.OpeningHours
import models.Tuesday
import testData.TestConstants.*

object BusinessListingServiceConstants {

  val testCreateBusinessAddressRequest: CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = userId1,
      businessId = businessId1,
      businessName = Some(businessName1),
      buildingName = Some(buildingName1),
      floorNumber = Some(floorNumber1),
      street = Some(street1),
      city = Some(city1),
      country = Some(country1),
      county = Some(county1),
      postcode = Some(postcode1),
      latitude = Some(latitude1),
      longitude = Some(longitude1)
    )

  val testCreateBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = userId1,
      businessId = businessId1,
      businessName = businessName1,
      description = businessDescription1,
      openingHours = List(
        OpeningHours(
          day = Monday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        ),
        OpeningHours(
          day = Tuesday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        )
      )
    )

  val testCreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = userId1,
      businessId = businessId1,
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1,
      websiteUrl = websiteUrl1
    )

  val businessListingRequest: BusinessListingRequest =
    BusinessListingRequest(
      businessId = businessId1,
      addressDetails = testCreateBusinessAddressRequest,
      businessSpecs = testCreateBusinessSpecificationsRequest,
      contactDetails = testCreateBusinessContactDetailsRequest,
      createdAt = createdAt01Jan2025,
      updatedAt = updatedAt01Jan2025
    )
}
