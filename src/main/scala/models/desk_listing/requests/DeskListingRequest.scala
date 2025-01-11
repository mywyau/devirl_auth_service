package models.desk_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.desk_listing.Availability
import models.desk_listing.DeskType

case class DeskListingRequest(
  deskName: String,
  description: Option[String],
  deskType: DeskType,
  quantity: Int,
  pricePerHour: BigDecimal,
  pricePerDay: BigDecimal,
  features: List[String], 
  availability: Availability,
  rules: Option[String]
)

object DeskListingRequest {
  implicit val eskRequestEncoder: Encoder[DeskListingRequest] = deriveEncoder[DeskListingRequest]
  implicit val eskRequestDecoder: Decoder[DeskListingRequest] = deriveDecoder[DeskListingRequest]
}
