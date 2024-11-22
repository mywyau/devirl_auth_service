package services.business.desk_listing

import models.business.desk_listing.errors.BusinessDeskErrors
import models.business.desk_listing.requests.DeskListingRequest
import models.business.desk_listing.service.DeskListing
import models.users.*


trait DeskListingServiceAlgebra[F[_]] {

  def findByUserId(userId: String): F[Either[BusinessDeskErrors, DeskListing]]

  def createDesk(businessDesk: DeskListingRequest): F[Either[BusinessDeskErrors, Int]]
}