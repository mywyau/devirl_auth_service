package controllers.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.PrivateDesk
import models.responses.CreatedResponse
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object DeskSpecificationsControllerConstants {

  val availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(10, 0, 0),
      endTime = LocalTime.of(10, 30, 0)
    )

  val testDeskSpecificationsRequest =
    UpdateDeskSpecificationsRequest(
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability
    )
}
