package services.business_address.validation

import cats.data.Validated
import cats.effect.Concurrent
import cats.implicits.*
import models.business.business_address.errors.*

class BusinessAddressValidationServiceImpl[F[_] : Concurrent] extends BusinessAddressValidationServiceAlgebra[F] {

  private def validateLength(field: String, min: Int, max: Int, error: BusinessAddressErrors): Validated[List[BusinessAddressErrors], String] =
    if (field.length >= min && field.length <= max) field.valid
    else List(error).invalid

  private def validateNonEmpty(field: String, error: BusinessAddressErrors): Validated[List[BusinessAddressErrors], String] = {
    if (field.trim.nonEmpty) field.valid
    else List(error).invalid
  }

  private def validatePattern(field: String, regex: String, error: BusinessAddressErrors): Validated[List[BusinessAddressErrors], String] =
    if (field.matches(regex)) field.valid
    else List(error).invalid

  private def validateStreetFormat(street: String, error: BusinessAddressErrors): Validated[List[BusinessAddressErrors], String] = {
    val streetRegex = """^[a-zA-Z0-9\s,'-]+$""" // Allows alphanumeric, spaces, commas, apostrophes, and hyphens
    validatePattern(street, streetRegex, error)
  }

  private def validateCityOrCountyName(name: String, error: BusinessAddressErrors): Validated[List[BusinessAddressErrors], String] = {
    val nameRegex = "^[a-zA-Z\\s'-]+$" // Allows letters, spaces, hyphens, and apostrophes
    validatePattern(name, nameRegex, error)
  }

  override def validateStreet(streetName: String): Validated[List[BusinessAddressErrors], String] = {
    (
      validateNonEmpty(streetName, BusinessEmptyStringField),
      validateLength(streetName, 3, 200, BusinessStreetLengthError),
      validateStreetFormat(streetName, BusinessInvalidFormat)
    ).tupled.map(_ => streetName)
  }

  override def validateCounty(county: String): Validated[List[BusinessAddressErrors], String] = {
    (
      validateNonEmpty(county, BusinessEmptyStringField),
      validateLength(county, 2, 200, BusinessCountyLengthError),
      validateCityOrCountyName(county, BusinessInvalidFormat)
    ).tupled.map(_ => county)
  }

  override def validatePostcode(postcode: String): Validated[List[BusinessAddressErrors], String] = {
    val postcodeRegex = "^[A-Z0-9 ]{5,10}$"
    (
      validateNonEmpty(postcode, BusinessEmptyStringField),
      validatePattern(postcode, postcodeRegex, BusinessPostcodeInvalidFormat)
    ).tupled.map(_ => postcode)
  }
}
