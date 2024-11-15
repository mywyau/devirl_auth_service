package controllers.users.wanderer_profile.constants

import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, WandererUserProfile}

import java.time.LocalDateTime

object WandererUserProfileControllerConstants {

  val sampleWandererUserProfile1: WandererUserProfile =
    WandererUserProfile(
      userId = "user_id_1",
      userLoginDetails = Some(
        UserLoginDetails(
          id = Some(1),
          user_id = "",
          username = "mikey5922",
          password_hash = "hashed_password",
          email = "user_id_1@gmail.com",
          role = Wanderer,
          created_at = LocalDateTime.of(2024, 10, 5, 15, 0),
          updated_at = LocalDateTime.of(2024, 10, 5, 15, 0)
        )
      ),
      first_name = Some("Michael"),
      last_name = Some("Yau"),
      userAddress = Some(
        UserAddress(
          userId = "user_id_1",
          street = "1 fake street",
          city = "cardiff",
          country = "United Kingdom",
          county = Some("South Glamorgan"),
          postcode = "CF3 5DE",
          created_at = LocalDateTime.of(2024, 10, 5, 15, 0),
          updated_at = LocalDateTime.of(2024, 10, 5, 15, 0)
        )
      ),
      contact_number = Some("07402205071"),
      email = Some("user_id_1@gmail.com"),
      company = Some("apple"),
      role = Some(Wanderer),
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0),
      updated_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}