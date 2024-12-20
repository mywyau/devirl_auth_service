package services.office.address.validation

import cats.data.Validated
import cats.effect.Concurrent
import cats.implicits.*
import models.business.address_details.errors.*
import models.office.address_details.errors.*

class OfficeAddressValidationServiceImpl[F[_] : Concurrent] extends OfficeAddressValidationServiceAlgebra[F] {

  private def validateLength(field: String, min: Int, max: Int, error: OfficeAddressErrors): Validated[List[OfficeAddressErrors], String] =
    if (field.length >= min && field.length <= max) field.valid
    else List(error).invalid

  private def validateNonEmpty(field: String, error: OfficeAddressErrors): Validated[List[OfficeAddressErrors], String] = {
    if (field.trim.nonEmpty) field.valid
    else List(error).invalid
  }

  private def validatePattern(field: String, regex: String, error: OfficeAddressErrors): Validated[List[OfficeAddressErrors], String] =
    if (field.matches(regex)) field.valid
    else List(error).invalid

  private def validateStreetFormat(street: String, error: OfficeAddressErrors): Validated[List[OfficeAddressErrors], String] = {
    val streetRegex = """^[a-zA-Z0-9\s,'-]+$""" // Allows alphanumeric, spaces, commas, apostrophes, and hyphens
    validatePattern(street, streetRegex, error)
  }

  private def validateCityOrCountyName(name: String, error: OfficeAddressErrors): Validated[List[OfficeAddressErrors], String] = {
    val nameRegex = "^[a-zA-Z\\s'-]+$" // Allows letters, spaces, hyphens, and apostrophes
    validatePattern(name, nameRegex, error)
  }

  override def validateStreet(streetName: String): Validated[List[OfficeAddressErrors], String] = {
    (
      validateNonEmpty(streetName, OfficeEmptyStringField),
      validateLength(streetName, 3, 200, OfficeStreetLengthError),
      validateStreetFormat(streetName, OfficeInvalidFormat)
    ).tupled.map(_ => streetName)
  }

  override def validateCounty(county: String): Validated[List[OfficeAddressErrors], String] = {
    (
      validateNonEmpty(county, OfficeEmptyStringField),
      validateLength(county, 2, 200, OfficeCountyLengthError),
      validateCityOrCountyName(county, OfficeInvalidFormat)
    ).tupled.map(_ => county)
  }

  override def validatePostcode(postcode: String): Validated[List[OfficeAddressErrors], String] = {
    val postcodeRegex = "^[A-Z0-9 ]{5,10}$"
    (
      validateNonEmpty(postcode, OfficeEmptyStringField),
      validatePattern(postcode, postcodeRegex, OfficePostcodeInvalidFormat)
    ).tupled.map(_ => postcode)
  }
}
