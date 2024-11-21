package services.wanderer_address.validation

import cats.data.Validated
import cats.effect.Concurrent
import cats.implicits.*
import models.users.wanderer_address.errors.*

class WandererAddressValidationServiceImpl[F[_] : Concurrent] extends WandererAddressValidationServiceAlgebra[F] {

  private def validateLength(field: String, min: Int, max: Int, error: WandererAddressErrors): Validated[List[WandererAddressErrors], String] =
    if (field.length >= min && field.length <= max) field.valid
    else List(error).invalid

  private def validateNonEmpty(field: String, error: WandererAddressErrors): Validated[List[WandererAddressErrors], String] = {
    if (field.trim.nonEmpty) field.valid
    else List(error).invalid
  }

  private def validatePattern(field: String, regex: String, error: WandererAddressErrors): Validated[List[WandererAddressErrors], String] =
    if (field.matches(regex)) field.valid
    else List(error).invalid

  private def validateStreetFormat(street: String, error: WandererAddressErrors): Validated[List[WandererAddressErrors], String] = {
    val streetRegex = """^[a-zA-Z0-9\s,'-]+$""" // Allows alphanumeric, spaces, commas, apostrophes, and hyphens
    validatePattern(street, streetRegex, error)
  }

  private def validateCityOrCountyName(name: String, error: WandererAddressErrors): Validated[List[WandererAddressErrors], String] = {
    val nameRegex = "^[a-zA-Z\\s'-]+$" // Allows letters, spaces, hyphens, and apostrophes
    validatePattern(name, nameRegex, error)
  }

  override def validateStreet(streetName: String): Validated[List[WandererAddressErrors], String] = {
    (
      validateNonEmpty(streetName, EmptyStringField),
      validateLength(streetName, 3, 200, StreetLengthError),
      validateStreetFormat(streetName, InvalidFormat)
    ).tupled.map(_ => streetName)
  }

  override def validateCounty(county: String): Validated[List[WandererAddressErrors], String] = {
    (
      validateNonEmpty(county, EmptyStringField),
      validateLength(county, 2, 200, CountyLengthError),
      validateCityOrCountyName(county, InvalidFormat)
    ).tupled.map(_ => county)
  }

  override def validatePostcode(postcode: String): Validated[List[WandererAddressErrors], String] = {
    val postcodeRegex = "^[A-Z0-9 ]{5,10}$"
    (
      validateNonEmpty(postcode, EmptyStringField),
      validatePattern(postcode, postcodeRegex, PostcodeInvalidFormat)
    ).tupled.map(_ => postcode)
  }
}
