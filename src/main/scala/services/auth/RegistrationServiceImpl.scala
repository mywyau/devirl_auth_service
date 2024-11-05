package services.auth

import cats.data.*
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.auth.{RegistrationValidation, UniqueUser}
import models.users.*
import models.users.database.UserLoginDetails
import models.users.requests.UserSignUpRequest
import repositories.users.UserLoginDetailsRepositoryAlgebra
import services.auth.algebra.{PasswordServiceAlgebra, RegistrationServiceAlgebra}


class RegistrationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                                             passwordService: PasswordServiceAlgebra[F]
                                                                           ) extends RegistrationServiceAlgebra[F] {

  def validateUnique(
                      field: String,
                      value: String,
                      lookup: String => F[Option[UserLoginDetails]]
                    ): F[ValidatedNel[String, RegistrationValidation]] = {
    lookup(value).map {
      case Some(_) => s"$field already exists".invalidNel
      case None => UniqueUser.validNel
    }
  }

  def uniqueUsernameAndEmail(request: UserSignUpRequest): F[Validated[List[String], RegistrationValidation]] = {
    val usernameValidation = validateUnique("username", request.username, userLoginDetailsRepo.findByUsername)
    val emailValidation = validateUnique("email", request.email, userLoginDetailsRepo.findByEmail)

    // Run validations in parallel and combine results
    (usernameValidation, emailValidation).parMapN { (usernameResult, emailResult) =>
      (usernameResult, emailResult)
        .mapN((_, _) => UniqueUser) // Combine into Valid if all succeed
        .leftMap(_.toList) // Flatten all accumulated errors into a single List[String]
    }
  }


  override def registerUser(request: UserSignUpRequest): F[Validated[List[String], UserLoginDetails]] = {

    val passwordValidationF: F[Validated[List[String], String]] =
      Concurrent[F].pure(passwordService.validatePassword(request.password))

    (passwordValidationF, uniqueUsernameAndEmail(request)).parMapN { (passwordValid, usernameAndEmailUnique) =>
      passwordValid.product(usernameAndEmailUnique).map(_ => request)
    }.flatMap {
      case Valid(request) =>
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
            else Validated.invalid(List("Failed to create user."))
          }
        }

      case Invalid(errors) =>
        // Collect all validation errors
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

