package models.office.office_specs.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import models.office.office_specs.OfficeAvailability

import java.time.LocalDateTime

case class OfficeSpecsRequest(
                               businessId: String,
                               officeId: String,
                               officeName: String,
                               description: String,
                               officeType: String,
                               numberOfFloors: Int,
                               capacity: Int,
                               amenities: List[String],
                               availability: OfficeAvailability,
                               rules: Option[String],
                               createdAt: LocalDateTime,
                               updatedAt: LocalDateTime
                             )

object OfficeSpecsRequest {
  implicit val officeSpecsRequestEncoder: Encoder[OfficeSpecsRequest] = deriveEncoder[OfficeSpecsRequest]
  implicit val officeSpecsRequestDecoder: Decoder[OfficeSpecsRequest] = deriveDecoder[OfficeSpecsRequest]
}

