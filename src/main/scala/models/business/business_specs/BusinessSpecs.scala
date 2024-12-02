package models.business.business_specs

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.business.business_specs.BusinessAvailability

import java.time.LocalDateTime

case class BusinessSpecs(
                        id: Option[Int],
                        userId: String,
                        businessId: String,
                        businessName: String,
                        description: String,
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime
                      )

object BusinessSpecs {
  implicit val businessSpecsEncoder: Encoder[BusinessSpecs] = deriveEncoder[BusinessSpecs]
  implicit val businessSpecsDecoder: Decoder[BusinessSpecs] = deriveDecoder[BusinessSpecs]
}
