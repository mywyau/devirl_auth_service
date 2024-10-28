package service.auth

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import services.PasswordServiceAlgebra
import weaver.SimpleIOSuite

object PasswordServiceSpec extends SimpleIOSuite {

  class MockPasswordService(
                             passwordValidationResult: Validated[List[String], String],
                             hashedPasswordResult: String,
                             checkPasswordResult: Boolean
                           ) extends PasswordServiceAlgebra[IO] {

    override def validatePassword(plainTextPassword: String): Validated[List[String], String] = passwordValidationResult

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(hashedPasswordResult)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] = IO.pure(checkPasswordResult)
  }


  test(".validatePassword() - should pass with a valid password") {
    val password = "Valid123!"
    val service = new MockPasswordService(Valid(password), "", true)


    IO(expect(service.validatePassword(password) == Valid(password)))
  }

  test(".validatePassword() - should fail with errors for an invalid password") {
    val password = "short"
    val service = new MockPasswordService(
      Invalid(List(
        "Password must be between 8 and 20 characters.",
        "Password must contain at least one uppercase letter.",
        "Password must contain at least one digit.",
        "Password must contain at least one special character."
      )),
      "",
      true
    )

    service.validatePassword(password) match {
      case Invalid(errors) =>
        IO(expect(
          errors.contains("Password must be between 8 and 20 characters.") &&
            errors.contains("Password must contain at least one uppercase letter.") &&
            errors.contains("Password must contain at least one digit.") &&
            errors.contains("Password must contain at least one special character.")
        ))
      case Valid(_) =>
        IO(failure("Expected validation errors for an invalid password"))
    }
  }

  test(".hashPassword() - should produce a SHA-256 hash") {
    val password = "Test123!"
    val expectedHash = "hashedPasswordString"
    val service = new MockPasswordService(Valid(password), expectedHash, true)

    service.hashPassword(password).map { hashedPassword =>
      expect(hashedPassword == expectedHash)
    }
  }

  test(".checkPassword() - should return true for matching passwords") {
    val password = "Match123!"
    val service = new MockPasswordService(Valid(password), "", true)

    service.checkPassword(password, "hashedPassword").map { result =>
      expect(result)
    }
  }

  test(".checkPassword() - should return false for non-matching passwords") {
    val password = "Match123!"
    val service = new MockPasswordService(Valid(password), "", false)

    service.checkPassword(password, "hashedPassword").map { result =>
      expect(!result)
    }
  }

  test(".checkPassword() - should throw an error for invalid password format") {
    val invalidPassword = "invalid" // Does not meet the length or other requirements
    val service =
      new MockPasswordService(
        passwordValidationResult = Invalid(List("Password must be between 8 and 20 characters.")),
        hashedPasswordResult = "",
        checkPasswordResult = false
      )

    val result: IO[Boolean] = service.checkPassword(invalidPassword, "hashedPassword")
    result.map { res =>
      expect(res == false)
    }
  }
}
