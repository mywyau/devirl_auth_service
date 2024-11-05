package models.users

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

object UserProfileRegistrationSpec$ extends SimpleIOSuite {

  val userRegistrationRequest1: UserRegistrationRequest =
    UserRegistrationRequest(
      userId = "user_id_1",
      username = "mikey5922",
      password = "hashed_password_1",
      first_name = "michael",
      last_name = "yau",
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 3NJ",
      contact_number = "07402205071",
      role = Wanderer,
      email = "mikey5922@gmail.com"
    )

  test("UserRegistrationRequest - encodes correctly to JSON") {

    val jsonResult = userRegistrationRequest1.asJson

    val expectedJson =
      """{
        |"userId":"user_id_1",
        |"username":"mikey5922",
        |"password":"hashed_password_1",
        |"first_name":"michael",
        |"last_name":"yau",
        |"street":"fake street 1",
        |"city":"fake city 1",
        |"country":"UK",
        |"county": "County 1",
        |"postcode":"CF3 3NJ",
        |"contact_number":"07402205071",
        |"role":"Wanderer",
        |"email":"mikey5922@gmail.com"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
