package controllers.users.wanderer_address.constants

import models.wanderer.wanderer_address.service.WandererAddress

import java.time.LocalDateTime

object WandererAddressControllerConstants {

  val sampleWandererAddress1: WandererAddress =
    WandererAddress(
      id = Some(1),
      userId = "user_1",
      street = Some("1 Canton Street"),
      city = Some("Cardiff"),
      country = Some("United Kingdom"),
      county = Some("South Glamorgan"),
      postcode = Some("CF3 5DE"),
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0),
      updatedAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}