package services.business.desk_listing.validation

import cats.data.Validated
import models.business.desk_listing.errors.BusinessDeskErrors

trait DeskListingValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[BusinessDeskErrors], String]

  def validatePostcode(postcode: String): Validated[List[BusinessDeskErrors], String]

  def validateCounty(county: String): Validated[List[BusinessDeskErrors], String]
}