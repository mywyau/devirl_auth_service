package services.constants

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.database.*
import models.office.address_details.OfficeAddressPartial
import models.office.address_details.requests.CreateOfficeAddressRequest
import testData.TestConstants.*

import java.time.LocalDateTime

object OfficeAddressServiceConstants {

  def testOfficeAddressPartial(businessId: String, officeId: String): OfficeAddressPartial =
    OfficeAddressPartial(
      businessId = businessId,
      officeId = officeId,
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
  

  def testCreateOfficeAddressRequest(businessId: String, officeId: String): CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = businessId,
      officeId = officeId,
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
}
