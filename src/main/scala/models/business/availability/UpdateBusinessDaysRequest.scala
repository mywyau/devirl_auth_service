package models.business.availability

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.Day

case class UpdateBusinessDaysRequest(
  days: List[Day]
)

object UpdateBusinessDaysRequest {
  implicit val encoder: Encoder[UpdateBusinessDaysRequest] = deriveEncoder[UpdateBusinessDaysRequest]
  implicit val decoder: Decoder[UpdateBusinessDaysRequest] = deriveDecoder[UpdateBusinessDaysRequest]
}
