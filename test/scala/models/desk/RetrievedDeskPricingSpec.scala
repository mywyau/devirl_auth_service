package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskPricing.RetrievedDeskPricing
import weaver.SimpleIOSuite

object RetrievedDeskPricingSpec extends SimpleIOSuite {

  val sampleRetrievedDeskPricing: RetrievedDeskPricing =
    RetrievedDeskPricing(
      pricePerHour = Some(30.00),
      pricePerDay = Some(180.00),
      pricePerWeek = Some(450.00),
      pricePerMonth = Some(1000.00),
      pricePerYear = Some(9000.00)
    )

  test("RetrievedDeskPricing model encodes correctly to JSON") {

    val jsonResult = sampleRetrievedDeskPricing.asJson

    val expectedJson =
      """
        |{
        |  "pricePerHour": 30.00,
        |  "pricePerDay": 180.00,
        |  "pricePerWeek": 450.00,
        |  "pricePerMonth": 1000.00,
        |  "pricePerYear": 9000.00
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
