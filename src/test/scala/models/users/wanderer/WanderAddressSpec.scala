package models.users.wanderer

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.wanderer.wanderer_address.service.WandererAddress
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WanderAddressSpec extends SimpleIOSuite {

  val sample_address: WandererAddress =
    WandererAddress(
      id = Some(1),
      userId = "user_id_1",
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      createdAt = LocalDateTime.of(2024, 10, 10, 10, 0),
      updatedAt = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("WandererAddress model encodes correctly to JSON") {

    val jsonResult = sample_address.asJson

    val expectedJson =
      """{
        |   "id" : 1,
        |   "userId" : "user_id_1",
        |   "street" : "fake street 1",
        |   "city" : "fake city 1",
        |   "country" : "UK",
        |   "county" : "County 1",
        |   "postcode" : "CF3 3NJ",
        |   "createdAt" : "2024-10-10T10:00:00",
        |   "updatedAt" : "2024-10-10T10:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
