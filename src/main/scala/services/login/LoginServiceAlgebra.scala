package services.login

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.login.adts.*
import models.users.login.requests.UserLoginRequest
import models.users.wanderer_profile.profile.UserLoginDetails
import repositories.users.UserLoginDetailsRepositoryAlgebra
import services.password.PasswordServiceAlgebra


trait LoginServiceAlgebra[F[_]] {

  def loginUser(request: UserLoginRequest): F[ValidatedNel[LoginError, UserLoginDetails]]
}


class LoginServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                      userLoginDetailsRepository: UserLoginDetailsRepositoryAlgebra[F],
                                                                      passwordService: PasswordServiceAlgebra[F]
                                                                    ) extends LoginServiceAlgebra[F] {

  override def loginUser(request: UserLoginRequest): F[ValidatedNel[LoginError, UserLoginDetails]] = {
    for {
      hashedPassword <- passwordService.hashPassword(request.password)
      result <- userLoginDetailsRepository.findByUsername(request.username).map {
        case Some(user) =>
          val passwordValidation =
            if (hashedPassword == user.passwordHash) Valid(user)
            else Invalid(NonEmptyList.of(LoginPasswordIncorrect))

          passwordValidation
        case None =>
          Invalid(NonEmptyList.of(UsernameNotFound))
      }
    } yield result
  }
}


object LoginServiceAlgebra {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userLoginDetailsRepository: UserLoginDetailsRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): LoginServiceAlgebra[F] =
    new LoginServiceImpl[F](userLoginDetailsRepository, passwordService)
}
