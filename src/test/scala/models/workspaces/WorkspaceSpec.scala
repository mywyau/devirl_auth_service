package models.workspaces

import cats.effect.IO
import io.circe._
import io.circe.parser._
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WorkspaceSpec extends SimpleIOSuite {

  val sample_workspace1: Workspace =
    Workspace(
      id = Some(1),
      business_id = "BUS123456",
      workspace_id = "WORK12345",
      name = "Downtown Tech Hub",
      description = "A modern coworking space with all amenities for tech startups.",
      address = "123 Main Street",
      city = "New York",
      country = "USA",
      postcode = "10001",
      price_per_day = BigDecimal(75.00),
      latitude = BigDecimal(40.7128),
      longitude = BigDecimal(-74.0060),
      created_at = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  test("Workspace model encodes correctly to JSON") {

    val jsonResult = sample_workspace1.asJson

    val expectedJson =
      """{
        |"id":1,
        |"business_id":"BUS123456",
        |"workspace_id":"WORK12345",
        |"name":"Downtown Tech Hub",
        |"description":"A modern coworking space with all amenities for tech startups.",
        |"address":"123 Main Street",
        |"city":"New York",
        |"country":"USA",
        |"postcode":"10001",
        |"price_per_day":75.0,
        |"latitude":40.7128,
        |"longitude":-74.006,
        |"created_at":"2024-10-10T10:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
