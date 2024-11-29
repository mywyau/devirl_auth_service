package services.office.office_listing

import cats.data.ValidatedNel
import models.database.SqlErrors
import models.office.office_listing.errors.OfficeListingErrors
import models.office.office_listing.requests.OfficeListingRequest
import models.office.office_listing.OfficeListing

trait OfficeListingServiceAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Either[OfficeListingErrors, OfficeListing]]

  def createOffice(officeListing: OfficeListingRequest): F[ValidatedNel[SqlErrors, Int]]
}