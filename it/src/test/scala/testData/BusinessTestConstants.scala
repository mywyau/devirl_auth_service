package testData

import java.time.LocalDateTime
import java.time.LocalTime
import models.business.address.CreateBusinessAddressRequest
import models.business.address.UpdateBusinessAddressRequest
import models.business.contact_details.UpdateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.UpdateBusinessSpecificationsRequest

import models.OpeningHours
import models.Monday
import models.Tuesday
import testData.TestConstants.*
import models.business.availability.*

object BusinessTestConstants {

  val businessOpeningHours1 =
      List(
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

  val testBusinessContactDetails: BusinessContactDetails =
    BusinessContactDetails(
      id = Some(1),
      userId = userId1,
      businessId = businessId1,
      businessName = Some(businessName1),
      primaryContactFirstName = Some(primaryContactFirstName1),
      primaryContactLastName = Some(primaryContactLastName1),
      contactEmail = Some(contactEmail1),
      contactNumber = Some(contactNumber1),
      websiteUrl = Some(websiteUrl1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

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

  val testUpdateBusinessAddressRequest: UpdateBusinessAddressRequest =
    UpdateBusinessAddressRequest(
      buildingName = Some(buildingName1),
      floorNumber = Some(floorNumber1),
      street = street1,
      city = city1,
      country = country1,
      county = county1,
      postcode = postcode1,
      latitude = latitude1,
      longitude = longitude1
    )

  val testUpdateBusinessContactDetailsRequest: UpdateBusinessContactDetailsRequest =
    UpdateBusinessContactDetailsRequest(
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1,
      websiteUrl = Some(websiteUrl1)
    )

  val testUpdateBusinessSpecificationsRequest: UpdateBusinessSpecificationsRequest =
    UpdateBusinessSpecificationsRequest(
      businessName = businessName1,
      description = businessDescription1,
      openingHours = businessOpeningHours1
    )
}
