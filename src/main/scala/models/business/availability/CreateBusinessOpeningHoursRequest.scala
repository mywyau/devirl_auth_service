package models.business.availability

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalTime
import models.Day

case class CreateBusinessOpeningHoursRequest(
  userId: String,
  businessId: String,
  openingTime: LocalTime,
  closingTime: LocalTime
)

object CreateBusinessOpeningHoursRequest {
  implicit val encoder: Encoder[CreateBusinessOpeningHoursRequest] = deriveEncoder[CreateBusinessOpeningHoursRequest]
  implicit val decoder: Decoder[CreateBusinessOpeningHoursRequest] = deriveDecoder[CreateBusinessOpeningHoursRequest]
}
