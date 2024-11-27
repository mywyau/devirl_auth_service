package models.office.office_contact_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class OfficeContactDetailsRequest(
                                        userId: String,
                                        street: String,
                                        city: String,
                                        country: String,
                                        county: Option[String],
                                        postcode: String,
                                        createdAt: LocalDateTime,
                                        updated_at: LocalDateTime
                                      )

object OfficeContactDetailsRequest {
  implicit val officeContactDetailsRequestEncoder: Encoder[OfficeContactDetailsRequest] = deriveEncoder[OfficeContactDetailsRequest]
  implicit val officeContactDetailsRequestDecoder: Decoder[OfficeContactDetailsRequest] = deriveDecoder[OfficeContactDetailsRequest]
}

