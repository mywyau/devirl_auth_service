package models

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.business.adts.PrivateDesk
import models.business.desk_listing.Availability
import models.business.desk_listing.requests.DeskListingRequest
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object DeskListingRequestSpec extends SimpleIOSuite {

  val availability: Availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
      endTime = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
    )

  val sampleDeskListingRequest: DeskListingRequest =
    DeskListingRequest(
      business_id = "business_123",
      workspace_id = "workspace_456",
      title = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      desk_type = PrivateDesk,
      quantity = 5,
      price_per_hour = BigDecimal(15.50),
      price_per_day = BigDecimal(120.00),
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability,
      created_at = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
      updated_at = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
    )

  test("DeskListingRequest model encodes correctly to JSON") {

    val jsonResult = sampleDeskListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "business_id": "business_123",
        |  "workspace_id": "workspace_456",
        |  "title": "Private Office Desk",
        |  "description": "A comfortable desk in a private office space with all amenities included.",
        |  "desk_type": "PrivateDesk",
        |  "quantity": 5,
        |  "price_per_hour": 15.50,
        |  "price_per_day": 120.00,
        |  "rules": "Please keep the desk clean and quiet.",
        |  "features": ["Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"],
        |  "availability": {
        |    "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |    "startTime": "2024-11-21T10:00:00",
        |    "endTime": "2024-11-21T10:30:00"
        |  },
        |  "created_at": "2024-11-21T10:00:00",
        |  "updated_at": "2024-11-21T10:30:00"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      //      _ <- IO(println(jsonResult.toString))
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }
}


