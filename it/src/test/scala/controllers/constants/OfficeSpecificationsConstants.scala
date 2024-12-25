package controllers.constants

import cats.effect.*
import models.office.adts.*
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeSpecificationsConstants {

  def testOfficeSpecs1(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications = {
    OfficeSpecifications(
      id = id,
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
      officeName = "Suburban Office",
      description = "A quiet office in the suburbs, perfect for focused work.",
      officeType = PrivateOffice,
      numberOfFloors = 1,
      totalDesks = 20,
      capacity = 40,
      amenities = List("Wi-Fi", "Tea", "Parking"),
      availability =
        OfficeAvailability(
          days = List("Monday", "Wednesday"),
          startTime = LocalTime.of(8, 0, 0),
          endTime = LocalTime.of(18, 0, 0)
        ),
      rules = Some("No pets. Maintain silence."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
