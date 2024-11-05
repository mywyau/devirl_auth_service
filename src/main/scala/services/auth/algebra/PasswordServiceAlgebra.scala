package services.auth.algebra

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*

import java.security.MessageDigest
import java.util.Base64

trait PasswordServiceAlgebra[F[_]] {

  def validatePassword(plainTextPassword: String): Validated[List[String], String]

  def hashPassword(plainTextPassword: String): F[String]

  def checkPassword(plainTextPassword: String, hashedPassword: String): F[Boolean]
}