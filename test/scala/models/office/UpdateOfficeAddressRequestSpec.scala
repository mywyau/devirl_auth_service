package models.office

import models.office.address_details.requests.UpdateOfficeAddressRequest
import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

object UpdateOfficeAddressRequestSpec extends SimpleIOSuite {

  val createOfficeAddressRequest: UpdateOfficeAddressRequest =
    UpdateOfficeAddressRequest(
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  test("UpdateOfficeAddressRequest model encodes correctly to JSON") {

    val jsonResult = createOfficeAddressRequest.asJson

    val expectedJson =
      """
        |{
        |   "buildingName": "build_123",
        |   "floorNumber": "floor 1",
        |   "street": "123 Main Street",
        |   "city": "New York",
        |   "country": "USA",
        |   "county": "New York County",
        |   "postcode": "10001",
        |   "latitude": 100.1,
        |   "longitude": -100.1
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
