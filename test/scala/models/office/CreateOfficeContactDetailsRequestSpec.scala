package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.ModelsBaseSpec
import testData.OfficeTestConstants.createOfficeContactDetailsRequest
import weaver.SimpleIOSuite

object CreateOfficeContactDetailsRequestSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("CreateOfficeContactDetailsRequest model encodes correctly to JSON") {

    val jsonResult = createOfficeContactDetailsRequest.asJson

    val expectedJson =
      """
        |{
        |   "businessId": "businessId1",
        |   "officeId": "officeId1",
        |   "primaryContactFirstName": "Michael",
        |   "primaryContactLastName": "Yau",
        |   "contactEmail": "mike@gmail.com",
        |   "contactNumber": "07402205071"
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
