package services.auth.algebra

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import models.auth.RegisterPasswordErrors

import java.security.MessageDigest
import java.util.Base64

trait PasswordServiceAlgebra[F[_]] {

  def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String]

  def hashPassword(plainTextPassword: String): F[String]

  def checkPassword(plainTextPassword: String, hashedPassword: String): F[Boolean]
}