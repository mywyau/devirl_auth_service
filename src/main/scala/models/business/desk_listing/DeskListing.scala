package models.business.desk_listing.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.business.adts.DeskType
import models.business.desk_listing.Availability

import java.time.LocalDateTime

case class DeskListing(
                         id: Option[Int],
                         business_id: String,
                         workspace_id: String,
                         title: String,
                         description: Option[String],
                         desk_type: DeskType,
                         quantity: Int,
                         price_per_hour: BigDecimal,
                         price_per_day: BigDecimal,
                         features: List[String],
                         availability: Availability,
                         rules: Option[String],
                         created_at: LocalDateTime,
                         updated_at: LocalDateTime
                       )

object DeskListing {
  implicit val businessDeskEncoder: Encoder[DeskListing] = deriveEncoder[DeskListing]
  implicit val businessDeskDecoder: Decoder[DeskListing] = deriveDecoder[DeskListing]
}
