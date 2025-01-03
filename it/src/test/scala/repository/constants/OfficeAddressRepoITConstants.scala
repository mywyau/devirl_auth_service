package repository.constants

import java.time.LocalDateTime
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial

object OfficeAddressRepoITConstants {

  def createInitialOfficeAddress(businessId: String, officeId: String): CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
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
      longitude = None
    )

  def testOfficeAddressRequest(businessId: String, officeId: String): CreateOfficeAddressRequest =
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

  def testOfficeAddressPartial(businessId: String, officeId: String): OfficeAddressPartial =
    OfficeAddressPartial(
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
