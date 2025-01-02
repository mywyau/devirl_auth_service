package models.business.specifications

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class BusinessSpecifications(
                                   id: Option[Int],
                                   userId: String,
                                   businessId: String,
                                   businessName: Option[String],
                                   description: Option[String],
                                   availability: Option[BusinessAvailability],
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                                 )

object BusinessSpecifications {

  implicit val businessSpecificationsEncoder: Encoder[BusinessSpecifications] = deriveEncoder[BusinessSpecifications]
  implicit val businessSpecificationsDecoder: Decoder[BusinessSpecifications] = deriveDecoder[BusinessSpecifications]
}
