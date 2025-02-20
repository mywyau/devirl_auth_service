package models.office.contact_details

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class UpdateOfficeContactDetailsRequest(
  primaryContactFirstName: String,
  primaryContactLastName: String,
  contactEmail: String,
  contactNumber: String
)

object UpdateOfficeContactDetailsRequest {
  implicit val updateOfficeContactDetailsRequestRequestEncoder: Encoder[UpdateOfficeContactDetailsRequest] = deriveEncoder[UpdateOfficeContactDetailsRequest]
  implicit val updateOfficeContactDetailsRequestRequestDecoder: Decoder[UpdateOfficeContactDetailsRequest] = deriveDecoder[UpdateOfficeContactDetailsRequest]
}
