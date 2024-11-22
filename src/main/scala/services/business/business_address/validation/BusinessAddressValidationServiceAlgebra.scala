package services.business.business_address.validation

import cats.data.Validated
import models.business.business_address.errors.BusinessAddressErrors

trait BusinessAddressValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[BusinessAddressErrors], String]

  def validatePostcode(postcode: String): Validated[List[BusinessAddressErrors], String]

  def validateCounty(county: String): Validated[List[BusinessAddressErrors], String]
}