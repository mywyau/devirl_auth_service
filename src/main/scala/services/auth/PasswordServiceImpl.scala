package services

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import models.auth.*
import services.auth.algebra.PasswordServiceAlgebra

import java.security.MessageDigest
import java.util.Base64


class PasswordServiceImpl[F[_] : Concurrent] extends PasswordServiceAlgebra[F] {

  private[services] def validateLength(password: String): Validated[List[RegisterPasswordErrors], String] =
    if (password.length >= 8 && password.length <= 20) password.valid
    else List(PasswordLengthError).invalid

  private[services] def validateUppercase(password: String): Validated[List[RegisterPasswordErrors], String] =
    if (password.exists(_.isUpper)) password.valid
    else List(PasswordNoUppercase).invalid

  private[services] def validateDigit(password: String): Validated[List[RegisterPasswordErrors], String] =
    if (password.exists(_.isDigit)) password.valid
    else List(PasswordNoDigit).invalid

  private[services] def validateSpecialChar(password: String): Validated[List[RegisterPasswordErrors], String] =
    if (password.exists(ch => "!@#$%^&*()_+-=[]|,./?><".contains(ch))) password.valid
    else List(PasswordNoSpecialCharacters).invalid

  private[services] def validateNoWhitespace(password: String): Validated[List[RegisterPasswordErrors], String] =
    if (!password.exists(_.isWhitespace)) password.valid
    else List(PasswordContainsWhitespace).invalid

  override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] = {
    (
      validateLength(plainTextPassword),
      validateUppercase(plainTextPassword),
      validateDigit(plainTextPassword),
      validateSpecialChar(plainTextPassword),
      validateNoWhitespace(plainTextPassword)
    ).mapN((_, _, _, _, _) => plainTextPassword)
  }

  override def hashPassword(plainTextPassword: String): F[String] =
    Concurrent[F].pure {
      val digest = MessageDigest.getInstance("SHA-256")
      val hashBytes = digest.digest(plainTextPassword.getBytes("UTF-8"))
      Base64.getEncoder.encodeToString(hashBytes)
    }

  override def checkPassword(plainTextPassword: String, hashedPassword: String): F[Boolean] = {
    val digest = MessageDigest.getInstance("SHA-256")
    val plainTextHash = Base64.getEncoder.encodeToString(digest.digest(plainTextPassword.getBytes("UTF-8")))

    Concurrent[F].pure {
      validatePassword(plainTextPassword) match {
        case Valid(_) =>
          plainTextHash == hashedPassword
        case Invalid(errors) =>
          false
        // TODO: Improve and fix this Mikey!!!!

        //          throw new IllegalArgumentException(errors.mkString("; "))
      }
    }
  }
}

object PasswordService {
  def apply[F[_] : Concurrent]: PasswordServiceAlgebra[F] = new PasswordServiceImpl[F]
}
