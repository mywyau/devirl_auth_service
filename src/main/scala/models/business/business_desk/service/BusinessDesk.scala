package models.business.business_desk.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.business.adts.DeskType

import java.time.LocalDateTime


case class BusinessDesk(
                         id: Option[Int],
                         business_id: String,
                         workspace_id: String,
                         title: String,
                         description: Option[String],
                         desk_type: DeskType,
                         price_per_hour: BigDecimal,
                         price_per_day: BigDecimal,
                         rules: Option[String],
                         created_at: LocalDateTime,
                         updated_at: LocalDateTime
                       )

object BusinessDesk {
  implicit val businessDeskEncoder: Encoder[BusinessDesk] = deriveEncoder[BusinessDesk]
  implicit val businessDeskDecoder: Decoder[BusinessDesk] = deriveDecoder[BusinessDesk]
}
