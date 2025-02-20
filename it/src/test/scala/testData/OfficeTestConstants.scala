package testData

import models.Monday
import models.OpeningHours
import models.Tuesday
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.RetrievedDeskPricing
import models.office.address_details.OfficeAddressPartial
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.UpdateOfficeAddressRequest
import models.office.OpenPlanOffice
import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.UpdateOfficeContactDetailsRequest
import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import models.office.specifications.CreateOfficeSpecificationsRequest
import models.office.specifications.UpdateOfficeSpecificationsRequest
import models.office_listing.requests.OfficeListingRequest
import testData.DeskTestConstants.description1
import testData.TestConstants.*

import java.time.LocalDateTime
import java.time.LocalTime

object OfficeTestConstants {

  val officeRules = "Please keep the office clean and tidy."

  val threeFloors = 3
  val threeDesks = 3
  val capacityOfFifty = 50

  val createOfficeAddressRequest: CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = businessId1,
      officeId = officeId1,
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

  val officeOpeningHours1: List[OpeningHours] =
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

  val createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = businessId1,
      officeId = officeId1,
      officeName = officeName1,
      description = officeDescription1,
      officeType = OpenPlanOffice,
      numberOfFloors = threeFloors,
      totalDesks = threeDesks,
      capacity = capacityOfFifty,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours = officeOpeningHours1,
      rules = Some(officeRules)
    )

  val createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId1,
      officeId = officeId1,
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1
    )

  val testOfficeSpecificationsPartial =
    OfficeSpecificationsPartial(
      businessId = businessId1,
      officeId = officeId1,
      officeName = Some(officeName1),
      description = Some(officeDescription1),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(threeFloors),
      totalDesks = Some(threeDesks),
      capacity = Some(capacityOfFifty),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking")),
      openingHours = Some(officeOpeningHours1),
      rules = Some(officeRules)
    )

  val officeSpecifications: OfficeSpecifications =
    OfficeSpecifications(
      id = Some(1),
      businessId = businessId1,
      officeId = officeId1,
      officeName = Some(officeName1),
      description = Some(officeDescription1),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(threeFloors),
      totalDesks = Some(threeDesks),
      capacity = Some(capacityOfFifty),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking")),
      openingHours = Some(officeOpeningHours1),
      rules = Some(officeRules),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val updateOfficeAddressRequest: UpdateOfficeAddressRequest =
    UpdateOfficeAddressRequest(
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

  val updateOfficeContactDetailsRequest: UpdateOfficeContactDetailsRequest =
    UpdateOfficeContactDetailsRequest(
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1
    )

  val updateOfficeSpecificationsRequest: UpdateOfficeSpecificationsRequest =
    UpdateOfficeSpecificationsRequest(
      officeName = officeName1,
      description = officeDescription1,
      officeType = OpenPlanOffice,
      numberOfFloors = threeFloors,
      totalDesks = threeDesks,
      capacity = capacityOfFifty,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours = officeOpeningHours1,
      rules = Some(officeRules)
    )

  val testCreateOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = businessId1,
      officeId = officeId1,
      officeName = officeName1,
      description = officeDescription1,
      officeType = OpenPlanOffice,
      numberOfFloors = threeFloors,
      totalDesks = threeDesks,
      capacity = capacityOfFifty,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
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
      ),
      rules = Some(officeRules)
    )

  val testCreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = businessId1,
      officeId = officeId1,
      buildingName = Some("butter building"),
      floorNumber = Some("floor 1"),
      street = Some("Main street 123"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("County 123"),
      postcode = Some("123456"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId1,
      officeId = officeId1,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  val officeListingRequest =
    OfficeListingRequest(
      officeId = officeId1,
      createOfficeAddressRequest = testCreateOfficeAddressRequest,
      createOfficeSpecificationsRequest = testCreateOfficeSpecificationsRequest,
      createOfficeContactDetailsRequest = testCreateOfficeContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
