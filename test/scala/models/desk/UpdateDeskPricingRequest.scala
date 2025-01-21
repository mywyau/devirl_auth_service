package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskPricing.UpdateDeskPricingRequest
import weaver.SimpleIOSuite

object UpdateDeskPricingRequestSpec extends SimpleIOSuite {

  test("UpdateDeskPricingRequest MAX model encodes correctly to JSON") {

    val sampleUpdateRequest: UpdateDeskPricingRequest =
      UpdateDeskPricingRequest(
        pricePerHour = 30.00,
        pricePerDay = Some(180.00),
        pricePerWeek = Some(450.00),
        pricePerMonth = Some(1000.00),
        pricePerYear = Some(9000.00)
      )

    val jsonResult = sampleUpdateRequest.asJson

    val expectedJson =
      """
        |{
        |  "pricePerHour": 30.0,
        |  "pricePerDay": 180.0,
        |  "pricePerWeek": 450.0,
        |  "pricePerMonth": 1000.0,
        |  "pricePerYear": 9000.0
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

  test("UpdateDeskPricingRequest MIN model encodes correctly to JSON") {

    val sampleUpdateRequestMin: UpdateDeskPricingRequest =
      UpdateDeskPricingRequest(
        pricePerHour = 30.00,
        pricePerDay = None,
        pricePerWeek = None,
        pricePerMonth = None,
        pricePerYear = None
      )

    val jsonResult = sampleUpdateRequestMin.asJson

    val expectedJson =
      """
        |{
        |  "pricePerHour": 30.0,
        |  "pricePerDay": null,
        |  "pricePerWeek": null,
        |  "pricePerMonth": null,
        |  "pricePerYear": null
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
