package services.login

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.login.requests.UserLoginRequest
import models.users.wanderer_profile.profile.UserLoginDetails
import repositories.users.{UserLoginDetailsRepositoryAlgebra, UserProfileRepositoryAlgebra}
import services.password.PasswordServiceAlgebra


trait LoginServiceAlgebra[F[_]] {

  def loginUser(request: UserLoginRequest): F[Either[String, UserLoginDetails]]
}


class LoginServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                      userLoginDetailsRepository: UserLoginDetailsRepositoryAlgebra[F],
                                                                      passwordService: PasswordServiceAlgebra[F]
                                                                    ) extends LoginServiceAlgebra[F] {

  override def loginUser(request: UserLoginRequest): F[Either[String, UserLoginDetails]] = {
    for {
      hashed_password <- passwordService.hashPassword(request.password)
      result: Either[String, UserLoginDetails] <-
        userLoginDetailsRepository.findByUsername(request.username).flatMap {
          case Some(user) if hashed_password == user.password_hash =>
            Concurrent[F].pure(Right(user))
          case Some(user) =>
            Concurrent[F].pure(Left("Invalid password"))
          case None =>
            Concurrent[F].pure(Left("Username not found"))
        }
    } yield {
      result
    }
  }
}


object LoginServiceAlgebra {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userLoginDetailsRepository: UserLoginDetailsRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): LoginServiceAlgebra[F] =
    new LoginServiceImpl[F](userLoginDetailsRepository, passwordService)
}

