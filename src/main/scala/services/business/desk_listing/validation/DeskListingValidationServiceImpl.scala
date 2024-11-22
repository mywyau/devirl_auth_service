package services.business.desk_listing.validation

import cats.data.Validated
import cats.effect.Concurrent
import cats.implicits.*
import models.business.desk_listing.errors.*

class DeskListingValidationServiceImpl[F[_] : Concurrent] extends DeskListingValidationServiceAlgebra[F] {

  private def validateLength(field: String, min: Int, max: Int, error: BusinessDeskErrors): Validated[List[BusinessDeskErrors], String] =
    if (field.length >= min && field.length <= max) field.valid
    else List(error).invalid

  private def validateNonEmpty(field: String, error: BusinessDeskErrors): Validated[List[BusinessDeskErrors], String] = {
    if (field.trim.nonEmpty) field.valid
    else List(error).invalid
  }

  private def validatePattern(field: String, regex: String, error: BusinessDeskErrors): Validated[List[BusinessDeskErrors], String] =
    if (field.matches(regex)) field.valid
    else List(error).invalid

  private def validateStreetFormat(street: String, error: BusinessDeskErrors): Validated[List[BusinessDeskErrors], String] = {
    val streetRegex = """^[a-zA-Z0-9\s,'-]+$""" // Allows alphanumeric, spaces, commas, apostrophes, and hyphens
    validatePattern(street, streetRegex, error)
  }

  private def validateCityOrCountyName(name: String, error: BusinessDeskErrors): Validated[List[BusinessDeskErrors], String] = {
    val nameRegex = "^[a-zA-Z\\s'-]+$" // Allows letters, spaces, hyphens, and apostrophes
    validatePattern(name, nameRegex, error)
  }

  override def validateStreet(streetName: String): Validated[List[BusinessDeskErrors], String] = {
    (
      validateNonEmpty(streetName, BusinessEmptyStringField),
      validateLength(streetName, 3, 200, BusinessDeskNotFound),
      validateStreetFormat(streetName, BusinessInvalidFormat)
    ).tupled.map(_ => streetName)
  }

  override def validateCounty(county: String): Validated[List[BusinessDeskErrors], String] = {
    (
      validateNonEmpty(county, BusinessEmptyStringField),
      validateLength(county, 2, 200, BusinessDeskNotFound),
      validateCityOrCountyName(county, BusinessInvalidFormat)
    ).tupled.map(_ => county)
  }

  override def validatePostcode(postcode: String): Validated[List[BusinessDeskErrors], String] = {
    val postcodeRegex = "^[A-Z0-9 ]{5,10}$"
    (
      validateNonEmpty(postcode, BusinessEmptyStringField),
      validatePattern(postcode, postcodeRegex, BusinessDeskNotFound)
    ).tupled.map(_ => postcode)
  }
}
