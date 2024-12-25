package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.adts.*
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object CreateOfficeContactDetailsRequestSpec extends SimpleIOSuite {

  val createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  test("CreateOfficeContactDetailsRequest model encodes correctly to JSON") {

    val jsonResult = createOfficeContactDetailsRequest.asJson

    val expectedJson =
      """
        |{
        |   "businessId": "business_id_1",
        |   "officeId": "office_id_1",
        |   "primaryContactFirstName": "Michael",
        |   "primaryContactLastName": "Yau",
        |   "contactEmail": "mike@gmail.com",
        |   "contactNumber": "07402205071"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }

}

