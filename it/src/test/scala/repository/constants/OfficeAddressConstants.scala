package repository.constants

import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest

import java.time.LocalDateTime

object OfficeAddressConstants {

  def testOfficeAddressRequest(businessId: String, officeId: String): CreateOfficeAddressRequest = {
    CreateOfficeAddressRequest(
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
      longitude = Some(-73.985428)
    )
  }

  def testOfficeAddress(id: Option[Int], businessId: String, officeId: String): OfficeAddress = {
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

}
