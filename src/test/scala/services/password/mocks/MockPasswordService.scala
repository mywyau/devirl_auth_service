package services.password.mocks

import cats.data.Validated
import cats.effect.IO
import models.users.adts.Role
import models.users.registration.*
import models.wanderer.wanderer_profile.profile.{UserLoginDetails, WandererUserProfile}
import repositories.user_profile.{UserLoginDetailsRepositoryAlgebra, UserProfileRepositoryAlgebra}
import services.auth.constants.AuthenticationServiceConstants.*
import services.authentication.password.PasswordServiceAlgebra

class MockPasswordService(expectedHash: String) extends PasswordServiceAlgebra[IO] {

  override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] =
    if (plainTextPassword.nonEmpty) Validated.valid(plainTextPassword) else Validated.invalid(List(PasswordLengthError))

  override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(expectedHash)

  override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] =
    IO.pure(expectedHash == hashedPassword)
}