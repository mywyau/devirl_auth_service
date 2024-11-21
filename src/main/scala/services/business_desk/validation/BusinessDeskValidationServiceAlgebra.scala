package services.business_desk.validation

import cats.data.Validated
import models.business.business_desk.errors.BusinessDeskErrors

trait BusinessDeskValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[BusinessDeskErrors], String]

  def validatePostcode(postcode: String): Validated[List[BusinessDeskErrors], String]

  def validateCounty(county: String): Validated[List[BusinessDeskErrors], String]
}