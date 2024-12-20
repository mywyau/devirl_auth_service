package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.business.contact_details.BusinessContactDetails
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessContactDetailsSpec extends SimpleIOSuite {

  val testBusinessContactDetails: BusinessContactDetails =
    BusinessContactDetails(
      id = Some(1),
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "mikey_corp",
      primaryContactFirstName = "Mikey",
      primaryContactLastName = "Yau",
      contactEmail = "mikey5922@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey5922.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test("BusinessContactDetails model encodes correctly to JSON") {

    val jsonResult = testBusinessContactDetails.asJson

    val expectedJson =
      """
        |{
        |  "id": 1,
        |  "userId": "user_id_1",
        |  "businessId": "business_id_1",
        |  "businessName": "mikey_corp",
        |  "primaryContactFirstName": "Mikey",
        |  "primaryContactLastName": "Yau",
        |  "contactEmail": "mikey5922@gmail.com",
        |  "contactNumber": "07402205071",
        |  "websiteUrl": "mikey5922.com",
        |  "createdAt": "2025-01-01T00:00:00",
        |  "updatedAt": "2025-01-01T00:00:00"
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

