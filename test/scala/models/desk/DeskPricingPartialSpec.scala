package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskPricing.DeskPricingPartial
import models.ModelsBaseSpec
import testData.DeskTestConstants.sampleDeskPricingPartial
import weaver.SimpleIOSuite

object DeskPricingPartialSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("DeskPricingPartial model encodes correctly to JSON") {

    val jsonResult = sampleDeskPricingPartial.asJson

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

    val jsonResultPretty = printer.print(jsonResult)
    val expectedResultPretty = printer.print(expectedResult)

    val differences = jsonDiff(jsonResult, expectedResult, expectedResultPretty, jsonResultPretty)

    for {
      _ <- IO {
        if (differences.nonEmpty) {
          println("=== JSON Difference Detected! ===")
          differences.foreach(diff => println(s"- $diff"))
          println("Generated JSON:\n" + jsonResultPretty)
          println("Expected JSON:\n" + expectedResultPretty)
        }
      }
    } yield expect(differences.isEmpty)
  }
}
