package controllers.constants

import cats.effect.*
import models.business.address.BusinessAddress
import models.business.address.requests.BusinessAddressRequest
import models.business.adts.*
import models.business.business_listing.requests.BusinessListingRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}

import java.time.LocalDateTime

object BusinessAddressConstants {

  def testBusinessAddress(id: Option[Int], userId: String, businessId: Option[String]): BusinessAddress = {
    BusinessAddress(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = Some("business_name_1"),
      buildingName = Some("building_name_1"),
      floorNumber = Some("floor_1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  def testBusinessAddressRequest(userId: String, businessId: Option[String]): BusinessAddressRequest = {
    BusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("business_name_1"),
      buildingName = Some("building_name_1"),
      floorNumber = Some("floor_1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
