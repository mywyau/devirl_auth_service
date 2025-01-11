package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.business.address.requests.UpdateBusinessAddressRequest
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UpdateBusinessAddressRequestSpec extends SimpleIOSuite {

  val testUpdateBusinessAddressRequest: UpdateBusinessAddressRequest =
    UpdateBusinessAddressRequest(
      buildingName = Some("Nameless Building"),
      floorNumber = Some("floor 1"),
      street = "123 Main Street",
      city = "New York",
      country = "USA",
      county = "New York County",
      postcode = "CF3 3NJ",
      latitude = 100.1,
      longitude = -100.1,
    )

  test("UpdateBusinessAddressRequest model encodes correctly to JSON") {

    val jsonResult = testUpdateBusinessAddressRequest.asJson

    val expectedJson =
      """
        |{
        |  "buildingName": "Nameless Building",
        |  "floorNumber": "floor 1",
        |  "street": "123 Main Street",
        |  "city": "New York",
        |  "country": "USA",
        |  "county": "New York County",
        |  "postcode": "CF3 3NJ",
        |  "latitude": 100.1,
        |  "longitude": -100.1
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

