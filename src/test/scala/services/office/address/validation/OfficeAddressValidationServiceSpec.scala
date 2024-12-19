package services.office.address.validation

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import models.office.office_address.errors.*
import services.office.address.validation.OfficeAddressValidationServiceImpl
import weaver.SimpleIOSuite

object OfficeAddressValidationServiceSpec extends SimpleIOSuite {

  private val validationService = new OfficeAddressValidationServiceImpl[IO]

  test(".validateStreet() - should pass for valid street names") {
    val result = validationService.validateStreet("123 Main Street")
    IO(expect(result == Valid("123 Main Street")))
  }

  test(".validateStreet() - should fail for missing street name") {
    val result = validationService.validateStreet("")
    IO(expect(result == Invalid(List(OfficeEmptyStringField, OfficeStreetLengthError, OfficeInvalidFormat))))
  }

  test(".validateStreet() - should fail for empty street name") {
    val result = validationService.validateStreet("")
    IO(expect(result == Invalid(List(OfficeEmptyStringField, OfficeStreetLengthError, OfficeInvalidFormat))))
  }

  test(".validateStreet() - should fail for too short street name") {
    val result = validationService.validateStreet("12")
    IO(expect(result == Invalid(List(OfficeStreetLengthError))))
  }

  test(".validateStreet() - should fail for too long street name") {
    val longStreet = "A" * 201
    val result = validationService.validateStreet(longStreet)
    IO(expect(result == Invalid(List(OfficeStreetLengthError))))
  }

  test(".validateCounty() - should pass for valid city names") {
    val result = validationService.validateCounty("New York")
    IO(expect(result == Valid("New York")))
  }

  test(".validateCounty() - should fail for empty city name") {
    val result = validationService.validateCounty("")
    IO(expect(result == Invalid(List(OfficeEmptyStringField, OfficeCountyLengthError, OfficeInvalidFormat))))
  }

  test(".validateCounty() - should fail for too short city name") {
    val result = validationService.validateCounty("A")
    IO(expect(result == Invalid(List(OfficeCountyLengthError))))
  }

  test(".validateCounty() - should fail for too long city name") {
    val longCity = "A" * 201
    val result = validationService.validateCounty(longCity)
    IO(expect(result == Invalid(List(OfficeCountyLengthError))))
  }

  test(".validatePostcode() - should pass for valid postcodes") {
    val result = validationService.validatePostcode("12345")
    IO(expect(result == Valid("12345")))
  }

  test(".validatePostcode() - should fail for missing postcode") {
    val result = validationService.validatePostcode("")
    IO(expect(result == Invalid(List(OfficeEmptyStringField, OfficePostcodeInvalidFormat))))
  }

  test(".validatePostcode() - should fail for empty postcode") {
    val result = validationService.validatePostcode("")
    IO(expect(result == Invalid(List(OfficeEmptyStringField, OfficePostcodeInvalidFormat))))
  }

  test(".validatePostcode() - should fail for invalid format") {
    val result = validationService.validatePostcode("Invalid_Postcode")
    IO(expect(result == Invalid(List(OfficePostcodeInvalidFormat))))
  }

  test(".validatePostcode() - should fail for too short postcode") {
    val result = validationService.validatePostcode("1234")
    IO(expect(result == Invalid(List(OfficePostcodeInvalidFormat))))
  }

  test(".validatePostcode() - should fail for too long postcode") {
    val result = validationService.validatePostcode("12345678901")
    IO(expect(result == Invalid(List(OfficePostcodeInvalidFormat))))
  }
}
