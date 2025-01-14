package models.constants

import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office_listing.requests.OfficeListingRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeListingConstants {

  val testCreateOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability =
        OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          startTime = LocalTime.of(10, 0, 0),
          endTime = LocalTime.of(10, 30, 0)
        ),
      rules = Some("No smoking. Maintain cleanliness.")
    )

  val testCreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  val officeListingRequest =
    OfficeListingRequest(
      officeId = "office_id_1",
      createOfficeAddressRequest = testCreateOfficeAddressRequest,
      createOfficeSpecificationsRequest = testCreateOfficeSpecificationsRequest,
      createOfficeContactDetailsRequest = testCreateOfficeContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
}
