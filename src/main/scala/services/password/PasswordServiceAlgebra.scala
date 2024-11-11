package services.password

import cats.data.Validated
import models.auth.RegisterPasswordErrors

trait PasswordServiceAlgebra[F[_]] {

  def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String]

  def hashPassword(plainTextPassword: String): F[String]

  def checkPassword(plainTextPassword: String, hashedPassword: String): F[Boolean]
}