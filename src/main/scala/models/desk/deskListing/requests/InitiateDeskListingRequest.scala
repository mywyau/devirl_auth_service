package models.desk.deskListing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime

case class InitiateDeskListingRequest(
  businessId: String,
  officeId: String,
  deskId: String,
  deskName: String,
  description: String
)

object InitiateDeskListingRequest {
  implicit val encoder: Encoder[InitiateDeskListingRequest] = deriveEncoder[InitiateDeskListingRequest]
  implicit val decoder: Decoder[InitiateDeskListingRequest] = deriveDecoder[InitiateDeskListingRequest]
}
