package services.authentication.registration

import cats.data.*
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.registration.*
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest
import repositories.user_profile.{UserLoginDetailsRepositoryAlgebra, WandererAddressRepositoryAlgebra, WandererPersonalDetailsRepositoryAlgebra}
import services.authentication.password.PasswordServiceAlgebra


class RegistrationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                                             wandererAddressRepo: WandererAddressRepositoryAlgebra[F],
                                                                             wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryAlgebra[F],
                                                                             passwordService: PasswordServiceAlgebra[F]
                                                                           ) extends RegistrationServiceAlgebra[F] {

  def validateUsernameUnique(value: String): F[ValidatedNel[RegistrationErrors, RegistrationValidation]] = {
    userLoginDetailsRepo.findByUsername(value).map {
      case Some(_) =>
        UsernameAlreadyExists.invalidNel
      case None =>
        UniqueUser.validNel
    }
  }


  def validateEmailUnique(value: String): F[ValidatedNel[RegistrationErrors, RegistrationValidation]] = {
    userLoginDetailsRepo.findByEmail(value).map {
      case Some(_) =>
        EmailAlreadyExists.invalidNel
      case None =>
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
      passwordValid.product(usernameAndEmailUnique).map(_ => request)
    }.flatMap {
      case Valid(request) =>

        for {
          hashedPassword <- passwordService.hashPassword(request.password)
          newUser = UserLoginDetails(
            id = None,
            userId = request.userId,
            username = request.username,
            passwordHash = hashedPassword,
            email = request.email,
            role = request.role,
            createdAt = request.createdAt,
            updatedAt = request.createdAt
          )
          _ <- wandererAddressRepo.createRegistrationWandererAddress(request.userId)
          _ <- wandererPersonalDetailsRepo.createRegistrationPersonalDetails(request.userId)
          createdUser <- userLoginDetailsRepo.createUserLoginDetails(newUser).map { rows =>
            if (rows > 0) Validated.valid(newUser)
            else Validated.invalid(List(CannotCreateUser))
          }
        } yield {
          createdUser
        }

      case Invalid(errors) =>
        Concurrent[F].pure(Validated.invalid(errors))
    }
  }
}

object RegistrationServiceImpl {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                   wandererAddressRepo: WandererAddressRepositoryAlgebra[F],
                                                   wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): RegistrationServiceAlgebra[F] =
    new RegistrationServiceImpl[F](userLoginDetailsRepo, wandererAddressRepo, wandererPersonalDetailsRepo, passwordService)
}

