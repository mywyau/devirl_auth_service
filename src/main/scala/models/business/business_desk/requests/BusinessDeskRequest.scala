package models.business.business_desk.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class BusinessDeskRequest(
                                business_id: String,
                                workspace_id: String,
                                title: String,
                                description: Option[String],
                                desk_type: String,
                                price_per_hour: BigDecimal,
                                price_per_day: BigDecimal,
                                rules: Option[String],
                                created_at: LocalDateTime,
                                updated_at: LocalDateTime,
                              )

object BusinessDeskRequest {
  implicit val businessDeskRequestEncoder: Encoder[BusinessDeskRequest] = deriveEncoder[BusinessDeskRequest]
  implicit val businessDeskRequestDecoder: Decoder[BusinessDeskRequest] = deriveDecoder[BusinessDeskRequest]
}

