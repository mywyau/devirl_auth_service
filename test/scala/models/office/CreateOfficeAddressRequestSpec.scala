package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.address_details.CreateOfficeAddressRequest

import models.ModelsBaseSpec
import testData.OfficeTestConstants.*
import weaver.SimpleIOSuite

object CreateOfficeAddressRequestSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("CreateOfficeAddressRequest model encodes correctly to JSON") {

    val jsonResult = createOfficeAddressRequest.asJson

    val expectedJson =
      """
        |{
        |   "businessId": "businessId1",
        |   "officeId": "officeId1",
        |   "buildingName": "butter building",
        |   "floorNumber": "floor 1",
        |   "street": "Main street 123",
        |   "city": "New York",
        |   "country": "USA",
        |   "county": "County 123",
        |   "postcode": "123456",
        |   "latitude": 100.1,
        |   "longitude": -100.1
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
