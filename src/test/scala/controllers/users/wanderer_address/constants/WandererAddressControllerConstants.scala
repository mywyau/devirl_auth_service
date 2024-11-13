package controllers.users.wanderer_address.constants

import models.users.wanderer_address.service.WandererAddress

import java.time.LocalDateTime

object WandererAddressControllerConstants {

  val sampleWandererAddress1: WandererAddress =
    WandererAddress(
      id = Some(1),
      user_id = "user_1",
      street = "1 Canton Street",
      city = "Cardiff",
      country = "United Kingdom",
      county = Some("South Glamorgan"),
      postcode = "CF3 5DE",
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0),
      updated_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}