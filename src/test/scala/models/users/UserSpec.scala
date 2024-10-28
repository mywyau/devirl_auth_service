package models.users

import cats.effect.IO
import io.circe._
import io.circe.parser._
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserSpec extends SimpleIOSuite {

  val sample_user1: User =
    User(
      username = "mikey5922",
      password_hash = "hashed_password_1",
      first_name = "michael",
      last_name = "yau",
      contact_number = "07402205071",
      email = "mikey5922@gmail.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("User model encodes correctly to JSON") {

    val jsonResult = sample_user1.asJson

    val expectedJson =
      """{
        |"username":"mikey5922",
        |"password_hash":"hashed_password_1",
        |"first_name":"michael",
        |"last_name":"yau",
        |"contact_number":"07402205071",
        |"email":"mikey5922@gmail.com",
        |"role":"Wanderer",
        |"created_at":"2024-10-10T10:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
