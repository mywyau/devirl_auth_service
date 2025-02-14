package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.ModelsBaseSpec
import models.desk.deskPricing.UpdateDeskPricingRequest
import testData.DeskTestConstants.*
import weaver.SimpleIOSuite

object UpdateDeskPricingRequestSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("UpdateDeskPricingRequest MAX model encodes correctly to JSON") {

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

  test("UpdateDeskPricingRequest MIN model encodes correctly to JSON") {

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
