package controllers.users.wanderer_profile.constants

import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}

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
      userPersonalDetails =
        Some(
          UserPersonalDetails(
            user_id = "user_id_1",
            first_name = Some("Michael"),
            last_name = Some("Yau"),
            contact_number = Some("07402205071"),
            email = Some("user_id_1@gmail.com"),
            company = Some("Meta"),
            created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
            updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
          )
        ),
      userAddress = Some(
        UserAddress(
          userId = "user_id_1",
          street = Some("1 fake street"),
          city = Some("cardiff"),
          country = Some("United Kingdom"),
          county = Some("South Glamorgan"),
          postcode = Some("CF3 5DE"),
          created_at = LocalDateTime.of(2024, 10, 5, 15, 0),
          updated_at = LocalDateTime.of(2024, 10, 5, 15, 0)
        )
      ),
      role = Some(Wanderer),
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0),
      updated_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}