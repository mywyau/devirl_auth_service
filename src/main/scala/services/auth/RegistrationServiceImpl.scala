package services.auth

import cats.data.*
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.auth.*
import models.users.*
import models.users.database.UserLoginDetails
import models.users.requests.UserSignUpRequest
import repositories.users.UserLoginDetailsRepositoryAlgebra
import services.auth.algebra.{PasswordServiceAlgebra, RegistrationServiceAlgebra}


class RegistrationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                                             passwordService: PasswordServiceAlgebra[F]
                                                                           ) extends RegistrationServiceAlgebra[F] {

  def validateUsernameUnique(value: String): F[ValidatedNel[RegistrationErrors, RegistrationValidation]] = {
    userLoginDetailsRepo.findByUsername(value).map {
      case Some(_) =>
        println("Username")
        UsernameAlreadyExists.invalidNel
      case None =>
        println("Oh no username")
        UniqueUser.validNel
    }
  }


  def validateEmailUnique(value: String): F[ValidatedNel[RegistrationErrors, RegistrationValidation]] = {
    userLoginDetailsRepo.findByEmail(value).map {
      case Some(_) =>
        println("Email")
        EmailAlreadyExists.invalidNel
      case None =>
        println("Oh no email")
        UniqueUser.validNel
    }
  }

  def uniqueUsernameAndEmail(request: UserSignUpRequest): F[Validated[List[RegistrationErrors], RegistrationValidation]] = {
    val usernameValidation = validateUsernameUnique(request.username)
    val emailValidation = validateEmailUnique(request.email)

    // Run validations in parallel and combine results
    (usernameValidation, emailValidation).parMapN { (usernameResult, emailResult) =>
      (usernameResult, emailResult)
        .mapN((_, _) => UniqueUser) // Combine into Valid if all succeed
        .leftMap(_.toList) // Flatten all accumulated errors into a single List[String]
    }
  }


  override def registerUser(request: UserSignUpRequest): F[Validated[List[RegistrationErrors], UserLoginDetails]] = {

    val passwordValidationF: F[Validated[List[RegisterPasswordErrors], String]] =
      Concurrent[F].pure(passwordService.validatePassword(request.password))

    (passwordValidationF, uniqueUsernameAndEmail(request)).parMapN { (passwordValid, usernameAndEmailUnique) =>
      println(passwordValid)
      passwordValid.product(usernameAndEmailUnique).map(_ => request)
    }.flatMap {
      case Valid(request) =>
        println("Here")
        passwordService.hashPassword(request.password).flatMap { hashedPassword =>
          val newUser =
            UserLoginDetails(
              None,
              user_id = request.user_id,
              username = request.username,
              password_hash = hashedPassword,
              email = request.email,
              role = request.role,
              created_at = request.created_at
            )
          userLoginDetailsRepo.createUserLoginDetails(newUser).map { rows =>
            if (rows > 0) Validated.valid(newUser)
            else Validated.invalid(List(CannotCreateUser))
          }
        }

      case Invalid(errors) =>
        println("There")
        Concurrent[F].pure(Validated.invalid(errors))
    }
  }
}

object RegistrationServiceImpl {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): RegistrationServiceAlgebra[F] =
    new RegistrationServiceImpl[F](userLoginDetailsRepo, passwordService)
}

