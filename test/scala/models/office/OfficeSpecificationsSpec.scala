package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.ModelsBaseSpec
import testData.OfficeTestConstants.*
import weaver.SimpleIOSuite

object OfficeSpecificationsSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("OfficeSpecifications model encodes correctly to JSON") {

    val jsonResult = officeSpecifications.asJson

    val expectedJson =
      """
        |{
        |  "amenities" : [
        |    "Wi-Fi",
        |    "Coffee Machine",
        |    "Projector",
        |    "Whiteboard",
        |    "Parking"
        |  ],
        |  "businessId" : "businessId1",
        |  "capacity" : 50,
        |  "createdAt" : "2025-01-01T00:00:00",
        |  "description" : "some office description",
        |  "id" : 1,
        |  "numberOfFloors" : 3,
        |  "officeId" : "officeId1",
        |  "officeName" : "Maginificanent Office",
        |  "officeType" : "OpenPlanOffice",
        |  "openingHours" : [
        |    {
        |      "closingTime" : "17:00:00",
        |      "day" : "Monday",
        |      "openingTime" : "09:00:00"
        |    },
        |    {
        |      "closingTime" : "17:00:00",
        |      "day" : "Tuesday",
        |      "openingTime" : "09:00:00"
        |    }
        |  ],
        |  "rules" : "Please keep the office clean and tidy.",
        |  "totalDesks" : 3,
        |  "updatedAt" : "2025-01-01T00:00:00"
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
