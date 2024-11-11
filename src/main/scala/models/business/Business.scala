package models.business

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class Business(
                     id: Option[Int],
                     business_id: String,
                     business_name: String,
                     contact_number: String,
                     contact_email: String,
                     created_at: LocalDateTime
                   )

object Business {
  implicit val businessEncoder: Encoder[Business] = deriveEncoder[Business]
  implicit val businessDecoder: Decoder[Business] = deriveDecoder[Business]
}

