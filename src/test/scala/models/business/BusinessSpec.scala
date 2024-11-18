package models.business

import cats.effect.IO
import io.circe._
import io.circe.parser._
import io.circe.syntax.EncoderOps
import models.business.Business
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessSpec extends SimpleIOSuite {

  val sampleBusiness_1: Business =
    Business(
      id = Some(1),
      businessId = "business_1",
      businessName = "Sample Business 1",
      contactNumber = "07402205071",
      contactEmail = "business@gmail.com",
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  test("Business model encodes correctly to JSON") {

    val jsonResult = sampleBusiness_1.asJson

    val expectedJson =
      """{
        |"id":1,
        |"businessId":"business_1",
        |"businessName":"Sample Business 1",
        |"contactNumber":"07402205071",
        |"contactEmail":"business@gmail.com",
        |"createdAt":"2024-10-05T15:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
