package services.desk_listing.validation

import cats.data.Validated
import models.business.desk_listing.errors.DeskListingErrors

trait DeskListingValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[DeskListingErrors], String]

  def validatePostcode(postcode: String): Validated[List[DeskListingErrors], String]

  def validateCounty(county: String): Validated[List[DeskListingErrors], String]
}
