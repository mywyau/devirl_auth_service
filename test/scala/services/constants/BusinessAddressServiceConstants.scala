package services.constants

import models.business.address.BusinessAddressPartial
import models.business.address.CreateBusinessAddressRequest
import models.database.*
import services.business.BusinessListingServiceImpl
import testData.TestConstants.*
import models.business.address.CreateBusinessAddressRequest
import models.business.address.BusinessAddressPartial

object BusinessAddressServiceConstants {

  def testBusinessAddressRequest(userId: String, businessId: String): CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some(businessName1),
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

  def testBusinessAddress(userId: String, businessId: String): BusinessAddressPartial =
    BusinessAddressPartial(
      userId = userId,
      businessId = businessId,
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
