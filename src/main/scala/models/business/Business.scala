package models.business

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class Business(
                     id: Option[Int],
                     businessId: String,
                     businessName: String,
                     contactNumber: String,
                     contactEmail: String,
                     createdAt: LocalDateTime
                   )

object Business {
  implicit val businessEncoder: Encoder[Business] = deriveEncoder[Business]
  implicit val businessDecoder: Decoder[Business] = deriveDecoder[Business]
}

