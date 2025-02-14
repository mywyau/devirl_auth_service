package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps

import models.ModelsBaseSpec
import testData.OfficeTestConstants.*
import weaver.SimpleIOSuite

object OfficeListingRequestSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("OfficeListingRequest model encodes correctly to JSON") {

    val jsonResult = officeListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "officeId": "officeId1",
        |  "createOfficeSpecificationsRequest": {
        |    "businessId": "businessId1",
        |    "officeId": "officeId1",
        |    "officeName": "Maginificanent Office",
        |    "description": "some office description",
        |    "officeType": "OpenPlanOffice",
        |    "numberOfFloors": 3,
        |    "totalDesks": 3,
        |    "capacity": 50,
        |    "openingHours" : [
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
        |    ],
        |    "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
        |    "rules": "Please keep the office clean and tidy."
        |  },
        |  "createOfficeAddressRequest": {
        |    "businessId": "businessId1",
        |    "officeId": "officeId1",
        |    "buildingName": "butter building",
        |    "floorNumber": "floor 1",
        |    "street": "Main street 123",
        |    "city": "New York",
        |    "country": "USA",
        |    "county": "County 123",
        |    "postcode": "123456",
        |    "latitude": 100.1,
        |    "longitude": -100.1
        |  },
        |  "createOfficeContactDetailsRequest": {
        |    "businessId": "businessId1",
        |    "officeId": "officeId1",
        |    "primaryContactFirstName": "Michael",
        |    "primaryContactLastName": "Yau",
        |    "contactEmail": "mike@gmail.com",
        |    "contactNumber": "07402205071"
        |  },
        |  "createdAt": "2025-01-01T00:00:00",
        |  "updatedAt": "2025-01-01T00:00:00"
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
