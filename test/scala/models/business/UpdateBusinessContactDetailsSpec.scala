package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest
import weaver.SimpleIOSuite

object UpdateBusinessContactDetailsRequestSpec extends SimpleIOSuite {

  val testUpdateBusinessContactDetailsRequest: UpdateBusinessContactDetailsRequest =
    UpdateBusinessContactDetailsRequest(
      primaryContactFirstName = "Mikey",
      primaryContactLastName = "Yau",
      contactEmail = "mikey5922@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey5922.com"
    )

  test("UpdateBusinessContactDetailsRequest model encodes correctly to JSON") {

    val jsonResult = testUpdateBusinessContactDetailsRequest.asJson

    val expectedJson =
      """
        |{
        |  "primaryContactFirstName": "Mikey",
        |  "primaryContactLastName": "Yau",
        |  "contactEmail": "mikey5922@gmail.com",
        |  "contactNumber": "07402205071",
        |  "websiteUrl": "mikey5922.com"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
