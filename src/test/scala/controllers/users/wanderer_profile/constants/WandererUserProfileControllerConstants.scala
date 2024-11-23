package controllers.users.wanderer_profile.constants

import models.users.adts.Wanderer
import models.wanderer.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}

import java.time.LocalDateTime

object WandererUserProfileControllerConstants {

  val sampleWandererUserProfile1: WandererUserProfile =
    WandererUserProfile(
      userId = "user_id_1",
      userLoginDetails = Some(
        UserLoginDetails(
          id = Some(1),
          userId = "",
          username = "mikey5922",
          passwordHash = "hashed_password",
          email = "user_id_1@gmail.com",
          role = Wanderer,
          createdAt = LocalDateTime.of(2024, 10, 5, 15, 0),
          updatedAt = LocalDateTime.of(2024, 10, 5, 15, 0)
        )
      ),
      userPersonalDetails =
        Some(
          UserPersonalDetails(
            userId = "user_id_1",
            firstName = Some("Michael"),
            lastName = Some("Yau"),
            contactNumber = Some("07402205071"),
            email = Some("user_id_1@gmail.com"),
            company = Some("Meta"),
            createdAt = LocalDateTime.of(2024, 10, 10, 10, 0),
            updatedAt = LocalDateTime.of(2024, 10, 10, 10, 0)
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
          createdAt = LocalDateTime.of(2024, 10, 5, 15, 0),
          updatedAt = LocalDateTime.of(2024, 10, 5, 15, 0)
        )
      ),
      role = Some(Wanderer),
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0),
      updatedAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}