package models.users

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererUserProfileSpec extends SimpleIOSuite {

  val sample_user1: WandererUserProfile =
    WandererUserProfile(
      userId = "user_id_1",
      userLoginDetails =
        Some(UserLoginDetails(
          id = Some(1),
          user_id = "user_id_1",
          username = "mikey5922",
          password_hash = "hashed_password_1",
          email = "mikey5922@gmail.com",
          role = Wanderer,
          created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
          updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
        )),
      userPersonalDetails =
        Some(
          UserPersonalDetails(
            user_id = "user_id_1",
            first_name = Some("michael"),
            last_name = Some("yau"),
            contact_number = Some("0123456789"),
            email = Some("mikey@gmail.com"),
            company = Some("Meta"),
            created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
            updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
          )
        ),
      userAddress =
        Some(UserAddress(
          userId = "user_id_1",
          street = Some("fake street 1"),
          city = Some("fake city 1"),
          country = Some("UK"),
          county = Some("County 1"),
          postcode = Some("CF3 3NJ"),
          created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
          updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
        )),
      role = Some(Wanderer),
      created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
      updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("User model encodes correctly to JSON") {

    val jsonResult = sample_user1.asJson

    val expectedJson =
      """
        |{
        |  "userId" : "user_id_1",
        |  "userLoginDetails" : {
        |    "id" : 1,
        |    "user_id" : "user_id_1",
        |    "username" : "mikey5922",
        |    "password_hash" : "hashed_password_1",
        |    "email" : "mikey5922@gmail.com",
        |    "role" : "Wanderer",
        |    "created_at" : "2024-10-10T10:00:00",
        |    "updated_at" : "2024-10-10T10:00:00"
        |  },
        |  "userPersonalDetails" : {
        |    "user_id" : "user_id_1",
        |    "first_name" : "michael",
        |    "last_name" : "yau",
        |    "contact_number" : "0123456789",
        |    "email" : "mikey@gmail.com",
        |    "company" : "Meta",
        |    "created_at" : "2024-10-10T10:00:00",
        |    "updated_at" : "2024-10-10T10:00:00"
        |  },
        |  "userAddress" : {
        |    "userId" : "user_id_1",
        |    "street" : "fake street 1",
        |    "city" : "fake city 1",
        |    "country" : "UK",
        |    "county" : "County 1",
        |    "postcode" : "CF3 3NJ",
        |    "created_at" : "2024-10-10T10:00:00",
        |    "updated_at" : "2024-10-10T10:00:00"
        |  },
        |  "role" : "Wanderer",
        |  "created_at" : "2024-10-10T10:00:00",
        |  "updated_at" : "2024-10-10T10:00:00"
        |}
        """.stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
//      _ <- IO(println(jsonResult.toString))
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }
}
