package models.users

import cats.effect.IO
import io.circe._
import io.circe.parser._
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessSpec extends SimpleIOSuite {

  val sampleBusiness_1: Business =
    Business(
      id = Some(1),
      business_id = "business_1",
      business_name = "Sample Business 1",
      contact_number = "07402205071",
      contact_email = "business@gmail.com",
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  test("Business model encodes correctly to JSON") {

    val jsonResult = sampleBusiness_1.asJson

    val expectedJson =
      """{
        |"id":1,
        |"business_id":"business_1",
        |"business_name":"Sample Business 1",
        |"contact_number":"07402205071",
        |"contact_email":"business@gmail.com",
        |"created_at":"2024-10-05T15:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
