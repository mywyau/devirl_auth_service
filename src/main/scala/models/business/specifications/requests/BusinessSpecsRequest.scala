package models.business.specifications.requests

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import models.business.specifications.BusinessAvailability

import java.time.LocalDateTime

case class BusinessSpecsRequest(
                                 businessId: String,
                                 businessName: String,
                                 description: String,
                                 businessType: String,
                                 numberOfFloors: Int,
                                 capacity: Int,
                                 amenities: List[String],
                                 availability: BusinessAvailability,
                                 rules: Option[String],
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime
                               )

object BusinessSpecsRequest {
  implicit val businessSpecsRequestEncoder: Encoder[BusinessSpecsRequest] = deriveEncoder[BusinessSpecsRequest]
  implicit val businessSpecsRequestDecoder: Decoder[BusinessSpecsRequest] = deriveDecoder[BusinessSpecsRequest]
}

