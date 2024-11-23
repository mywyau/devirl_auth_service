package models.users.wanderer

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.adts.Wanderer
import models.wanderer.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererUserProfileSpec extends SimpleIOSuite {

  val sample_user1: WandererUserProfile =
    WandererUserProfile(
      userId = "user_id_1",
      userLoginDetails =
        Some(UserLoginDetails(
          id = Some(1),
          userId = "user_id_1",
          username = "mikey5922",
          passwordHash = "hashed_password_1",
          email = "mikey5922@gmail.com",
          role = Wanderer,
          createdAt = LocalDateTime.of(2024, 10, 10, 10, 0),
          updatedAt = LocalDateTime.of(2024, 10, 10, 10, 0)
        )),
      userPersonalDetails =
        Some(
          UserPersonalDetails(
            userId = "user_id_1",
            firstName = Some("michael"),
            lastName = Some("yau"),
            contactNumber = Some("0123456789"),
            email = Some("mikey@gmail.com"),
            company = Some("Meta"),
            createdAt = LocalDateTime.of(2024, 10, 10, 10, 0),
            updatedAt = LocalDateTime.of(2024, 10, 10, 10, 0)
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
          createdAt = LocalDateTime.of(2024, 10, 10, 10, 0),
          updatedAt = LocalDateTime.of(2024, 10, 10, 10, 0)
        )),
      role = Some(Wanderer),
      createdAt = LocalDateTime.of(2024, 10, 10, 10, 0),
      updatedAt = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("User model encodes correctly to JSON") {

    val jsonResult = sample_user1.asJson

    val expectedJson =
      """
        |{
        |  "userId" : "user_id_1",
        |  "userLoginDetails" : {
        |    "id" : 1,
        |    "userId" : "user_id_1",
        |    "username" : "mikey5922",
        |    "passwordHash" : "hashed_password_1",
        |    "email" : "mikey5922@gmail.com",
        |    "role" : "Wanderer",
        |    "createdAt" : "2024-10-10T10:00:00",
        |    "updatedAt" : "2024-10-10T10:00:00"
        |  },
        |  "userPersonalDetails" : {
        |    "userId" : "user_id_1",
        |    "firstName" : "michael",
        |    "lastName" : "yau",
        |    "contactNumber" : "0123456789",
        |    "email" : "mikey@gmail.com",
        |    "company" : "Meta",
        |    "createdAt" : "2024-10-10T10:00:00",
        |    "updatedAt" : "2024-10-10T10:00:00"
        |  },
        |  "userAddress" : {
        |    "userId" : "user_id_1",
        |    "street" : "fake street 1",
        |    "city" : "fake city 1",
        |    "country" : "UK",
        |    "county" : "County 1",
        |    "postcode" : "CF3 3NJ",
        |    "createdAt" : "2024-10-10T10:00:00",
        |    "updatedAt" : "2024-10-10T10:00:00"
        |  },
        |  "role" : "Wanderer",
        |  "createdAt" : "2024-10-10T10:00:00",
        |  "updatedAt" : "2024-10-10T10:00:00"
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
