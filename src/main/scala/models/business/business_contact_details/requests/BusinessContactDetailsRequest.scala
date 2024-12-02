package models.business.business_contact_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class BusinessContactDetailsRequest(
                                        userId: String,
                                        street: String,
                                        city: String,
                                        country: String,
                                        county: Option[String],
                                        postcode: String,
                                        createdAt: LocalDateTime,
                                        updated_at: LocalDateTime
                                      )

object BusinessContactDetailsRequest {
  implicit val businessContactDetailsRequestEncoder: Encoder[BusinessContactDetailsRequest] = deriveEncoder[BusinessContactDetailsRequest]
  implicit val businessContactDetailsRequestDecoder: Decoder[BusinessContactDetailsRequest] = deriveDecoder[BusinessContactDetailsRequest]
}

