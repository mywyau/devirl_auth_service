package models.business.business_specs

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.business.business_specs.BusinessAvailability

import java.time.LocalDateTime

case class BusinessSpecifications(
                                   id: Option[Int],
                                   userId: String,
                                   businessId: String,
                                   businessName: String,
                                   description: String,
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                                 )

object BusinessSpecifications {

  implicit val businessSpecificationsEncoder: Encoder[BusinessSpecifications] = deriveEncoder[BusinessSpecifications]
  implicit val businessSpecificationsDecoder: Decoder[BusinessSpecifications] = deriveDecoder[BusinessSpecifications]
}
