package testData

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.RetrievedDeskPricing
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddressPartial
import models.office.adts.OpenPlanOffice
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications
import models.Monday
import models.OpeningHours
import models.Tuesday
import testData.DeskTestConstants.description1
import testData.TestConstants.*
import models.office_listing.requests.OfficeListingRequest

object OfficeTestConstants {

  val officeRules = "Please keep the office clean and tidy."

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
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
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

  val officeSpecifications: OfficeSpecifications =
    OfficeSpecifications(
      id = Some(1),
      businessId = businessId1,
      officeId = officeId1,
      officeName = Some(officeName1),
      description = Some("some office description"),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(3),
      totalDesks = Some(3),
      capacity = Some(50),
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
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours = officeOpeningHours1,
      rules = Some(officeRules)
    )

  val testCreateOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      officeName = "Maginificanent Office",
      description = "some office description",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
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
      rules = Some("Please keep the office clean and tidy.")
    )

  val testCreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = "businessId1",
      officeId = "officeId1",
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
      businessId = "businessId1",
      officeId = "officeId1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  val officeListingRequest =
    OfficeListingRequest(
      officeId = "officeId1",
      createOfficeAddressRequest = testCreateOfficeAddressRequest,
      createOfficeSpecificationsRequest = testCreateOfficeSpecificationsRequest,
      createOfficeContactDetailsRequest = testCreateOfficeContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
