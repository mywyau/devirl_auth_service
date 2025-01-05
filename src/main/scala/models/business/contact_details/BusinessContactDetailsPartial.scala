package models.business.contact_details

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class BusinessContactDetailsPartial(
  userId: String,
  businessId: String,
  primaryContactFirstName: Option[String],
  primaryContactLastName: Option[String],
  contactEmail: Option[String],
  contactNumber: Option[String],
  websiteUrl: Option[String]
)

object BusinessContactDetailsPartial {
  implicit val encoder: Encoder[BusinessContactDetailsPartial] = deriveEncoder[BusinessContactDetailsPartial]
  implicit val decoder: Decoder[BusinessContactDetailsPartial] = deriveDecoder[BusinessContactDetailsPartial]
}
