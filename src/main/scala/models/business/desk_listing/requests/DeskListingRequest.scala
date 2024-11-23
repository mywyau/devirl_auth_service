package models.business.desk_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability

import java.time.LocalDateTime

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
                                updated_at: LocalDateTime,
                              )

object DeskListingRequest {
  implicit val businessDeskRequestEncoder: Encoder[DeskListingRequest] = deriveEncoder[DeskListingRequest]
  implicit val businessDeskRequestDecoder: Decoder[DeskListingRequest] = deriveDecoder[DeskListingRequest]
}

