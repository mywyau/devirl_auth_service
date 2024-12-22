package controllers.constants

import cats.effect.*
import models.office.address_details.OfficeAddress

import java.time.LocalDateTime

object OfficeAddressConstants {

  def testOfficeAddress1(id: Option[Int], businessId: String, officeId: String): OfficeAddress = {
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = officeId,
      buildingName = Some("Empire State Building"),
      floorNumber = Some("5th Floor"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10001"),
      latitude = Some(40.748817),
      longitude = Some(-73.985428),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def testOfficeAddress2(id: Option[Int], businessId: String, officeId: String): OfficeAddress = {
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = officeId,
      buildingName = Some("One World Trade Center"),
      floorNumber = Some("15th Floor"),
      street = Some("200 Greenwich Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10007"),
      latitude = Some(40.712742),
      longitude = Some(-74.013382),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def testOfficeAddress3(id: Option[Int], businessId: String, officeId: String): OfficeAddress = {
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = officeId,
      buildingName = Some("Chrysler Building"),
      floorNumber = Some("10th Floor"),
      street = Some("405 Lexington Avenue"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10174"),
      latitude = Some(40.751652),
      longitude = Some(-73.975311),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }
}
