package controllers.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.Availability
import models.desk_listing.PrivateDesk
import models.responses.CreatedResponse
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object DeskListingControllerConstants {

  val availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(10, 0, 0),
      endTime = LocalTime.of(10, 30, 0)
    )

  val testDeskListingRequest =
    DeskListingRequest(
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = PrivateDesk,
      quantity = 5,
      pricePerHour = BigDecimal(15.50),
      pricePerDay = BigDecimal(120.00),
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability
    )
}
