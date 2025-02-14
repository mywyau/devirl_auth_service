package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalTime
import models.desk.deskListing.DeskListing
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import models.ModelsBaseSpec
import models.Monday
import models.OpeningHours
import testData.DeskTestConstants.*
import weaver.SimpleIOSuite

object DeskListingSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("DeskListing model encodes correctly to JSON") {

    val jsonResult = sampleDeskListing.asJson

    val expectedJson =
      """
      |{
      |  "deskId" : "deskId1",
      |  "pricing" : {
      |    "pricePerDay" : 180.0,
      |    "pricePerHour" : 30.0,
      |    "pricePerMonth" : 1000.0,
      |    "pricePerWeek" : 450.0,
      |    "pricePerYear" : 9000.0
      |  },
      |  "specifications" : {
      |    "description" : "Some description",
      |    "deskId" : "deskId1",
      |    "deskName" : "Luxury supreme desk",
      |    "deskType" : "PrivateDesk",
      |    "features" : [
      |      "Wi-Fi",
      |      "Power Outlets",
      |      "Monitor",
      |      "Ergonomic Chair"
      |    ],
      |    "openingHours" : [
      |      {
      |        "closingTime" : "17:00:00",
      |        "day" : "Monday",
      |        "openingTime" : "09:00:00"
      |      },
      |      {
      |        "closingTime" : "17:00:00",
      |        "day" : "Tuesday",
      |        "openingTime" : "09:00:00"
      |      }
      |    ],
      |    "quantity" : 5,
      |    "rules" : "Please keep the desk clean and quiet."
      |  }
      |}
      """.stripMargin

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
