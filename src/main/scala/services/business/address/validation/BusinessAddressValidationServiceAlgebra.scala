package services.business.address.validation

import cats.data.Validated
import models.business.address_details.errors.BusinessAddressErrors

trait BusinessAddressValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[BusinessAddressErrors], String]

  def validatePostcode(postcode: String): Validated[List[BusinessAddressErrors], String]

  def validateCounty(county: String): Validated[List[BusinessAddressErrors], String]
}