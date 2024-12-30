package controllers.constants

import cats.effect.*
import models.office.adts.*
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeSpecificationsControllerITConstants {

  def testCreateOfficeSpecificationsRequest(businessId: String, officeId: String): CreateOfficeSpecificationsRequest = {
    CreateOfficeSpecificationsRequest(
      businessId = businessId,
      officeId = officeId,
      officeName = "Downtown Workspace",
      description = "A modern co-working space located in the heart of downtown.",
      officeType = PrivateOffice,
      numberOfFloors = 2,
      totalDesks = 50,
      capacity = 100,
      amenities = List("Wi-Fi", "Coffee Machine", "Meeting Rooms"),
      availability =
        OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          startTime = LocalTime.of(8, 0, 0),
          endTime = LocalTime.of(18, 0, 0)
        ),
      rules = Some("No loud conversations. Keep the desks clean.")
    )
  }

  def testOfficeSpecs1(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications = {
    OfficeSpecifications(
      id = id,
      businessId = businessId,
      officeId = officeId,
      officeName = Some("Downtown Workspace"),
      description = Some("A modern co-working space located in the heart of downtown."),
      officeType = Some(PrivateOffice),
      numberOfFloors = Some(2),
      totalDesks = Some(50),
      capacity = Some(100),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Meeting Rooms")),
      availability =
        Some(
          OfficeAvailability(
            days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
            startTime = LocalTime.of(8, 0, 0),
            endTime = LocalTime.of(18, 0, 0)
          )
        ),
      rules = Some("No loud conversations. Keep the desks clean."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def testOfficeSpecs2(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications = {
    OfficeSpecifications(
      id = id,
      businessId = businessId,
      officeId = officeId,
      officeName = Some("Suburban Office"),
      description = Some("A quiet office in the suburbs, perfect for focused work."),
      officeType = Some(PrivateOffice),
      numberOfFloors = Some(1),
      totalDesks = Some(20),
      capacity = Some(40),
      amenities = Some(List("Wi-Fi", "Tea", "Parking")),
      availability =
        Some(
          OfficeAvailability(
            days = List("Monday", "Wednesday"),
            startTime = LocalTime.of(8, 0, 0),
            endTime = LocalTime.of(18, 0, 0)
          )
        ),
      rules = Some("No pets. Maintain silence."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
