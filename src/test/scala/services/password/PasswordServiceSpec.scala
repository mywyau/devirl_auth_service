package services.password

import cats.effect.IO
import services.authentication.password.PasswordServiceImpl
import weaver.{Expectations, SimpleIOSuite}

object PasswordServiceSpec extends SimpleIOSuite {

  val passwordService = new PasswordServiceImpl[IO]

  test("validateLength should pass for passwords within the required length") {
    IO(expect(passwordService.validateLength("ValidLength123!").isValid))
  }

  test("validateLength should fail for passwords that are shorter than 8 chars") {
    IO(expect(
      passwordService.validateLength("Short1!").isInvalid
    ))
  }

  test("validateLength should fail for passwords that are shorter than 20 chars") {
    IO(expect(
      passwordService.validateLength("ThisPasswordIsWayTooLong123!").isInvalid
    ))
  }

  test("validateUppercase should pass for passwords containing an uppercase letter") {
    IO(
      expect(passwordService.validateUppercase("ValidPassword1!").isValid)
    )
  }

  test("validateUppercase should fail for passwords without an uppercase letter") {
    IO(expect(passwordService.validateUppercase("invalidpassword1!").isInvalid))
  }

  test("validateDigit should pass for passwords containing a digit") {
    IO(
      expect(passwordService.validateDigit("Password1!").isValid)
    )
  }

  test("validateDigit should fail for passwords without a digit") {
    IO(
      expect(passwordService.validateDigit("Password!").isInvalid)
    )
  }

  test("validateSpecialChar should pass for passwords containing a special character") {
    IO(
      expect(passwordService.validateSpecialChar("Password1!").isValid)
    )
  }

  test("validateSpecialChar should fail for passwords without a special character") {
    IO(
      expect(passwordService.validateSpecialChar("Password1").isInvalid)
    )
  }

  test("validateNoWhitespace should pass for passwords without whitespace") {
    IO(
      expect(passwordService.validateNoWhitespace("Password1!").isValid)
    )
  }

  test("validateNoWhitespace should fail for passwords with whitespace") {
    IO(
      expect(passwordService.validateNoWhitespace("Password 1!").isInvalid)
    )
  }

  test("validatePassword should return Valid for a strong password") {
    IO(
      expect(passwordService.validatePassword("ValidPassword1!").isValid)
    )
  }

  test("validatePassword should return Invalid with errors for a weak password") {
    IO(
      expect(passwordService.validatePassword("weak").isInvalid)
    )
  }

  test("hashPassword should produce a consistent hash") {
    val password = "StrongPassword1!"
    for {
      hash1 <- passwordService.hashPassword(password)
      hash2 <- passwordService.hashPassword(password)
    } yield expect.same(hash1, hash2) and expect(hash1.nonEmpty)
  }

  test("checkPassword should return true for a correct password") {
    val password = "CorrectPassword1!"
    for {
      hashed <- passwordService.hashPassword(password)
      result <- passwordService.checkPassword(password, hashed)
    } yield expect(result)
  }

  test("checkPassword should return false for an incorrect password") {
    val password = "CorrectPassword1!"
    val incorrectPassword = "WrongPassword1!"
    for {
      hashed <- passwordService.hashPassword(password)
      result <- passwordService.checkPassword(incorrectPassword, hashed)
    } yield expect(result == false)
  }

  test("checkPassword should throw an exception for an invalid password") {

    passwordService.checkPassword("short", "dummyHash").map(
      result => expect(result == false)
    )
  }
}
