package controllers.business.business_address.constants

import models.business.address.BusinessAddress
import java.time.LocalDateTime

object BusinessAddressControllerConstants {

  val sampleBusinessAddress1: BusinessAddress =
    BusinessAddress(
      id = Some(1),
      userId = "user_1",
      businessId = Some("business1"),
      businessName = Some("mikeyCorp"),
      buildingName = Some("building 1"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("Cardiff"),
      country = Some("United Kingdom"),
      county = Some("South Glamorgan"),
      postcode = Some("CF3 5DE"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0),
      updatedAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}