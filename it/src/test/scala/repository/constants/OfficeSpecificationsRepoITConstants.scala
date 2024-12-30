package repository.constants

import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.OpenPlanOffice
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeSpecificationsRepoITConstants {

  def testCreateOfficeSpecificationsRequest(businessId: String, officeId: String): CreateOfficeSpecificationsRequest = {
    CreateOfficeSpecificationsRequest(
      businessId = businessId,
      officeId = officeId,
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
  }


  def testOfficeSpecs(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications = {
    OfficeSpecifications(
      id = Some(1),
      businessId = businessId,
      officeId = officeId,
      officeName = Some("Modern Workspace"),
      description = Some("A vibrant office space in the heart of the city, ideal for teams or individuals."),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(3),
      totalDesks = Some(3),
      capacity = Some(50),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking")),
      availability =
        Some(OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          startTime = LocalTime.of(10, 0, 0),
          endTime = LocalTime.of(10, 30, 0)
        )),
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
