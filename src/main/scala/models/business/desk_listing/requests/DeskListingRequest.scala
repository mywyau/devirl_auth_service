package models.business.desk_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.business.adts.DeskType
import models.business.desk_listing.Availability

case class DeskListingRequest(
  business_id: String,
  workspace_id: String,
  title: String,
  description: Option[String],
  desk_type: DeskType,
  quantity: Int,
  price_per_hour: BigDecimal,
  price_per_day: BigDecimal,
  rules: Option[String],
  features: List[String], // Stored as PostgreSQL TEXT[]
  availability: Availability, // Stored as PostgreSQL JSONB
  created_at: LocalDateTime,
  updated_at: LocalDateTime
)

object DeskListingRequest {
  implicit val eskRequestEncoder: Encoder[DeskListingRequest] = deriveEncoder[DeskListingRequest]
  implicit val eskRequestDecoder: Decoder[DeskListingRequest] = deriveDecoder[DeskListingRequest]
}
