package models.business.contact_details

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class BusinessContactDetails(
  id: Option[Int],
  userId: String,
  businessId: String,
  businessName: Option[String],
  primaryContactFirstName: Option[String],
  primaryContactLastName: Option[String],
  contactEmail: Option[String],
  contactNumber: Option[String],
  websiteUrl: Option[String],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object BusinessContactDetails {
  implicit val encoder: Encoder[BusinessContactDetails] = deriveEncoder[BusinessContactDetails]
  implicit val decoder: Decoder[BusinessContactDetails] = deriveDecoder[BusinessContactDetails]
}
