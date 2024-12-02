package services.business.business_listing

import cats.data.ValidatedNel
import models.database.SqlErrors
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.business_listing.BusinessListing



trait BusinessListingServiceAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Either[BusinessListingErrors, BusinessListing]]

  def createBusiness(businessListing: BusinessListingRequest): F[ValidatedNel[SqlErrors, Int]]
}