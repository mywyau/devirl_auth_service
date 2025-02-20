package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import models.office.address_details.OfficeAddressPartial
import models.office.address_details.CreateOfficeAddressRequest
import testData.TestConstants.*

import java.time.LocalDateTime

object OfficeAddressRepoConstants {

  def testOfficeAddressRequest(businessId: String, officeId: String): CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
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

}
