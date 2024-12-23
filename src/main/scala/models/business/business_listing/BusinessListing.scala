package models.business.business_listing

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.address.BusinessAddress
import models.business.adts.DeskType
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import models.business.desk_listing.Availability

import java.time.LocalDateTime

case class BusinessListing(
                            id: Option[Int],
                            userId: String,
                            businessId: String,
                            businessSpecs: BusinessSpecifications,
                            addressDetails: BusinessAddress,
                            availability: BusinessAvailability,
                            createdAt: LocalDateTime,
                            updatedAt: LocalDateTime
                          )

object BusinessListing {
  implicit val businessListingRequestEncoder: Encoder[BusinessListing] = deriveEncoder[BusinessListing]
  implicit val businessListingRequestDecoder: Decoder[BusinessListing] = deriveDecoder[BusinessListing]
}

