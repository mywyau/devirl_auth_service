package models.office.contact_details

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class OfficeContactDetailsPartial(
  businessId: String,
  officeId: String,
  primaryContactFirstName: Option[String],
  primaryContactLastName: Option[String],
  contactEmail: Option[String],
  contactNumber: Option[String]
)

object OfficeContactDetailsPartial {
  implicit val encoder: Encoder[OfficeContactDetailsPartial] = deriveEncoder[OfficeContactDetailsPartial]
  implicit val decoder: Decoder[OfficeContactDetailsPartial] = deriveDecoder[OfficeContactDetailsPartial]
}
