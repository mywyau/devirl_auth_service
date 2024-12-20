package services.office.address.validation

import cats.data.Validated
import models.office.address_details.errors.*

trait OfficeAddressValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[OfficeAddressErrors], String]

  def validatePostcode(postcode: String): Validated[List[OfficeAddressErrors], String]

  def validateCounty(county: String): Validated[List[OfficeAddressErrors], String]
}