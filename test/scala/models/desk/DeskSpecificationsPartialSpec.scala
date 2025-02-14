package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.ModelsBaseSpec
import testData.DeskTestConstants.sampleDeskSpecificationsPartial
import weaver.SimpleIOSuite

object DeskSpecificationsPartialSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("DeskSpecificationsPartial model encodes correctly to JSON") {

    val jsonResult = sampleDeskSpecificationsPartial.asJson

    val expectedJson =
      """
        |{
        |  "deskId": "deskId1",
        |  "deskName": "Luxury supreme desk",
        |  "description": "Some description",
        |  "deskType": "PrivateDesk",
        |  "quantity": 5,
        |  "rules": "Please keep the desk clean and quiet.",
        |  "features": ["Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"],
        |  "openingHours" : [
        |     {
        |       "day" : "Monday",
        |       "openingTime" : "09:00:00",
        |       "closingTime" : "17:00:00"
        |     },
        |     {
        |       "day" : "Tuesday",
        |       "openingTime" : "09:00:00",
        |       "closingTime" : "17:00:00"
        |     }
        |  ]
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
