package controllers.constants

import cats.effect.*
import io.circe.Json
import io.circe.syntax.*
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.office_listing.requests.OfficeListingRequest
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

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


  def testOfficeListingRequest(officeId: String): OfficeListingRequest =
    OfficeListingRequest(
      officeId = officeId,
      createOfficeAddressRequest = testOfficeAddressRequest,
      createOfficeSpecificationsRequest = testCreateOfficeSpecificationsRequest,
      createOfficeContactDetailsRequest = testCreateOfficeContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
