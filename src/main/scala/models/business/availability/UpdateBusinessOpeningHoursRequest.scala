package models.business.availability

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder

import java.time.LocalTime
import models.Day

case class UpdateBusinessOpeningHoursRequest(
  userId: String,
  businessId: String,
  day: Day,
  openingTime: LocalTime,
  closingTime: LocalTime
)

object UpdateBusinessOpeningHoursRequest {
  implicit val encoder: Encoder[UpdateBusinessOpeningHoursRequest] = deriveEncoder[UpdateBusinessOpeningHoursRequest]
  implicit val decoder: Decoder[UpdateBusinessOpeningHoursRequest] = deriveDecoder[UpdateBusinessOpeningHoursRequest]
}
