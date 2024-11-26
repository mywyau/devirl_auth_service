package services.office.office_address.validation

import cats.data.Validated
import models.office.office_address.errors.*

trait OfficeAddressValidationServiceAlgebra[F[_]] {

  def validateStreet(streetName: String): Validated[List[OfficeAddressErrors], String]

  def validatePostcode(postcode: String): Validated[List[OfficeAddressErrors], String]

  def validateCounty(county: String): Validated[List[OfficeAddressErrors], String]
}