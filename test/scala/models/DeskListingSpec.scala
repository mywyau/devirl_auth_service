package models

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import weaver.SimpleIOSuite

object DeskListingSpec extends SimpleIOSuite {

  val availability: Availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(10, 0, 0),
      endTime = LocalTime.of(10, 30, 0)
    )

  val sampleDeskSpecificationsPartial: DeskSpecificationsPartial =
    DeskSpecificationsPartial(
      deskId = "desk001",
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = Some(PrivateDesk),
      quantity = Some(5),
      rules = Some("Please keep the desk clean and quiet."),
      features = Some(List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair")),
      availability = Some(availability)
    )

  val sampleDeskPricingPartial: RetrievedDeskPricing =
    RetrievedDeskPricing(
      pricePerHour = Some(30.00),
      pricePerDay = Some(180.00),
      pricePerWeek = Some(450.00),
      pricePerMonth = Some(1000.00),
      pricePerYear = Some(9000.00)
    )

  val sampleDeskListing: DeskListing =
    DeskListing(
      deskId = "desk001",
      sampleDeskSpecificationsPartial,
      sampleDeskPricingPartial
    )

  test("DeskListing model encodes correctly to JSON") {

    val jsonResult = sampleDeskListing.asJson

    val expectedJson =
      """
        |{
        |  "deskId" : "desk001",
        |  "specifications" : {
        |    "deskId" : "desk001",
        |    "deskName" : "Private Office Desk",
        |    "description" : "A comfortable desk in a private office space with all amenities included.",
        |    "deskType" : "PrivateDesk",
        |    "quantity" : 5,
        |    "features" : [
        |      "Wi-Fi",
        |      "Power Outlets",
        |      "Monitor",
        |      "Ergonomic Chair"
        |    ],
        |    "availability" : {
        |      "days" : [
        |        "Monday",
        |        "Tuesday",
        |        "Wednesday",
        |        "Thursday",
        |        "Friday"
        |      ],
        |      "startTime" : "10:00:00",
        |      "endTime" : "10:30:00"
        |    },
        |    "rules" : "Please keep the desk clean and quiet."
        |  },
        |  "pricing" : {
        |    "pricePerHour" : 30.0,
        |    "pricePerDay" : 180.0,
        |    "pricePerWeek" : 450.0,
        |    "pricePerMonth" : 1000.0,
        |    "pricePerYear" : 9000.0
        |  }
        |}
      """.stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
