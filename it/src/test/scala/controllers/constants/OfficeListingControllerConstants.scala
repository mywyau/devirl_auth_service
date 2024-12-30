package controllers.constants

import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.office_listing.OfficeListing
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeListingControllerConstants {

  val testOfficeAvailability: OfficeAvailability =
    OfficeAvailability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(0, 0, 0),
      endTime = LocalTime.of(0, 0, 0)
    )

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
      availability = testOfficeAvailability,
      rules = Some("No smoking. Maintain cleanliness.")
    )

  val testOfficeAddressRequest: CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      buildingName = Some("OfficeListingControllerISpec Building"),
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


  def testInitiateOfficeListingRequest(businessId: String, officeId: String): InitiateOfficeListingRequest =
    InitiateOfficeListingRequest(
      businessId = businessId,
      officeId = officeId,
    )

  def officeSpecifications(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications =
    OfficeSpecifications(
      id = id,
      businessId = businessId,
      officeId = officeId,
      officeName = None,
      description = None,
      officeType = None,
      numberOfFloors = None,
      totalDesks = None,
      capacity = None,
      amenities = None,
      availability = None,
      rules = None,
      createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
    )

  def officeAddress(id: Option[Int], businessId: String, officeId: String): OfficeAddress =
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = officeId,
      buildingName = None,
      floorNumber = None,
      street = None,
      city = None,
      country = None,
      county = None,
      postcode = None,
      latitude = None,
      longitude = None,
      createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
    )

  def testContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = None,
      primaryContactLastName = None,
      contactEmail = None,
      contactNumber = None,
      createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
    )

  def testOfficeListing(id: Option[Int], businessId: String, officeId: String): OfficeListing = {
    OfficeListing(
      officeId = officeId,
      officeAddressDetails = officeAddress(id, businessId, officeId),
      officeSpecifications = officeSpecifications(id, businessId, officeId),
      officeContactDetails = testContactDetails(id, businessId, officeId)
    )
  }
}
