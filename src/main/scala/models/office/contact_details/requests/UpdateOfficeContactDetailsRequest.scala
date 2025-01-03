package models.office.contact_details.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class UpdateOfficeContactDetailsRequest(
  primaryContactFirstName: String,
  primaryContactLastName: String,
  contactEmail: String,
  contactNumber: String,
  updatedAt: LocalDateTime
)

object UpdateOfficeContactDetailsRequest {
  implicit val updateOfficeContactDetailsRequestRequestEncoder: Encoder[UpdateOfficeContactDetailsRequest] = deriveEncoder[UpdateOfficeContactDetailsRequest]
  implicit val updateOfficeContactDetailsRequestRequestDecoder: Decoder[UpdateOfficeContactDetailsRequest] = deriveDecoder[UpdateOfficeContactDetailsRequest]
}
