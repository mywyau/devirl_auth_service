package services.wanderer_address.validation

import cats.data.Validated
import models.users.wanderer_address.errors.WandererAddressErrors

trait WandererAddressValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[WandererAddressErrors], String]
  
  def validatePostcode(postcode: String): Validated[List[WandererAddressErrors], String]

  def validateCounty(county: String): Validated[List[WandererAddressErrors], String]
}