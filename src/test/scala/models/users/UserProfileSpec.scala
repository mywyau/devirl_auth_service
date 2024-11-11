package models.users

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserProfileSpec extends SimpleIOSuite {

  val sample_user1: UserProfile =
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        id = Some(1),
        user_id = "user_id_1",
        username = "mikey5922",
        password_hash = "hashed_password_1",
        email = "mikey5922@gmail.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
        updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
      ),
      first_name = "michael",
      last_name = "yau",
      UserAddress(
        userId = "user_id_1",
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
        updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
      ),
      contact_number = "07402205071",
      email = "mikey5922@gmail.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
      updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("User model encodes correctly to JSON") {

    val jsonResult = sample_user1.asJson

    val expectedJson =
      """{
        |"userId" : "user_id_1",
        |"userLoginDetails" : {
        |   "id": 1,
        |   "user_id" : "user_id_1",
        |   "username" : "mikey5922",
        |   "password_hash" : "hashed_password_1",
        |   "email": "mikey5922@gmail.com",
        |   "role": "Wanderer",
        |   "created_at" : "2024-10-10T10:00:00",
        |   "updated_at" : "2024-10-10T10:00:00"
        |},
        |"first_name" : "michael",
        |"last_name" : "yau",
        |"userAddress" : {
        |   "userId" : "user_id_1",
        |   "street" : "fake street 1",
        |   "city" : "fake city 1",
        |   "country" : "UK",
        |   "county" : "County 1",
        |   "postcode" : "CF3 3NJ",
        |   "created_at" : "2024-10-10T10:00:00",
        |   "updated_at" : "2024-10-10T10:00:00"
        | },
        |"contact_number" : "07402205071",
        |"email": "mikey5922@gmail.com",
        |"role": "Wanderer",
        |"created_at" : "2024-10-10T10:00:00",
        |"updated_at" : "2024-10-10T10:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      //      _ <- IO(println(jsonResult.toString))
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }
}
