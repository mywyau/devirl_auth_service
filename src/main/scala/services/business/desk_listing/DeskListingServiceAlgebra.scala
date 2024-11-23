package services.business.desk_listing

import models.business.desk_listing.errors.DeskListingErrors
import models.business.desk_listing.requests.DeskListingRequest
import models.business.desk_listing.service.DeskListing
import models.users.*

trait DeskListingServiceAlgebra[F[_]] {

  def findByUserId(userId: String): F[Either[DeskListingErrors, DeskListing]]

  def createDesk(DeskListing: DeskListingRequest): F[Either[DeskListingErrors, Int]]
}