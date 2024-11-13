package models.users.wanderer_address

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.wanderer_address.service.WandererAddress
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WanderAddressSpec extends SimpleIOSuite {

  val sample_address: WandererAddress =
    WandererAddress(
      id = Some(1),
      user_id = "user_id_1",
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 3NJ",
      created_at = LocalDateTime.of(2024, 10, 10, 10, 0),
      updated_at = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("WandererAddress model encodes correctly to JSON") {

    val jsonResult = sample_address.asJson

    val expectedJson =
      """{
        |   "id" : 1,
        |   "user_id" : "user_id_1",
        |   "street" : "fake street 1",
        |   "city" : "fake city 1",
        |   "country" : "UK",
        |   "county" : "County 1",
        |   "postcode" : "CF3 3NJ",
        |   "created_at" : "2024-10-10T10:00:00",
        |   "updated_at" : "2024-10-10T10:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
