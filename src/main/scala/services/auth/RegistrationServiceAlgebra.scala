package services.auth

import cats.data.*
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.auth.{RegistrationValidation, UniqueUser}
import models.users.*
import repositories.users.UserProfileRepositoryAlgebra
import services.auth.algebra.{PasswordServiceAlgebra, RegistrationServiceAlgebra}

import java.time.LocalDateTime


class RegistrationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             userRepository: UserProfileRepositoryAlgebra[F],
                                                                             passwordService: PasswordServiceAlgebra[F]
                                                                           ) extends RegistrationServiceAlgebra[F] {

  def validateUnique(
                      field: String,
                      value: String,
                      lookup: String => F[Option[UserProfile]]
                    ): F[ValidatedNel[String, RegistrationValidation]] = {
    lookup(value).map {
      case Some(_) => s"$field already exists".invalidNel
      case None => UniqueUser.validNel
    }
  }

  def uniqueUser(request: UserRegistrationRequest): F[Validated[List[String], RegistrationValidation]] = {
    val usernameValidation = validateUnique("username", request.username, userRepository.findByUsername)
    val contactValidation = validateUnique("contact_number", request.contact_number, userRepository.findByContactNumber)
    val emailValidation = validateUnique("email", request.email, userRepository.findByEmail)

    // Run validations in parallel and combine results
    (usernameValidation, contactValidation, emailValidation).parMapN { (usernameResult, contactResult, emailResult) =>
      (usernameResult, contactResult, emailResult)
        .mapN((_, _, _) => UniqueUser) // Combine into Valid if all succeed
        .leftMap(_.toList) // Flatten all accumulated errors into a single List[String]
    }
  }


  override def registerUser(request: UserRegistrationRequest): F[Validated[List[String], UserProfile]] = {

    val passwordValidationF: F[Validated[List[String], String]] =
      Concurrent[F].pure(passwordService.validatePassword(request.password))

    (passwordValidationF, uniqueUser(request)).parMapN { (passwordValid, uniqueValid) =>
      passwordValid.product(uniqueValid).map(_ => request)
    }.flatMap {
      case Valid(request) =>
        passwordService.hashPassword(request.password).flatMap { hashedPassword =>
          val newUser =
            UserProfile(
              userId = request.userId,
              UserLoginDetails(
                userId = request.userId,
                username = request.username,
                password_hash = hashedPassword
              ),
              first_name = request.first_name,
              last_name = request.last_name,
              UserAddress(
                userId = request.userId,
                street = request.street,
                city = request.city,
                country = request.country,
                county = request.county,
                postcode = request.postcode,
                created_at = LocalDateTime.now()
              ),
              contact_number = request.contact_number,
              email = request.email,
              role = request.role,
              created_at = LocalDateTime.now()
            )
          userRepository.createUserProfile(newUser).map { rows =>
            if (rows > 0) Validated.valid(newUser)
            else Validated.invalid(List("Failed to create user."))
          }
        }

      case Invalid(errors) =>
        // Collect all validation errors
        Concurrent[F].pure(Validated.invalid(errors))
    }
  }

//  override def signUp(request: SignUpRequest): F[Validated[List[String], UserProfile]] = {
//    val passwordValidationF: F[Validated[List[String], String]] =
//      Concurrent[F].pure(passwordService.validatePassword(request.password))
//
//    (passwordValidationF, uniqueUser(request)).parMapN { (passwordValid, uniqueValid) =>
//      passwordValid.product(uniqueValid).map(_ => request)
//    }.flatMap {
//      case Valid(request) =>
//        passwordService.hashPassword(request.password).flatMap { hashedPassword =>
//          userRepository.createUser(newUser).map { rows =>
//            if (rows > 0) Validated.valid(newUser)
//            else Validated.invalid(List("Failed to create user."))
//          }
//        }
//
//      case Invalid(errors) =>
//        // Collect all validation errors
//        Concurrent[F].pure(Validated.invalid(errors))
//    }
//  }

}

object RegistrationServiceAlgebra {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userRepository: UserProfileRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): RegistrationServiceAlgebra[F] =
    new RegistrationServiceImpl[F](userRepository, passwordService)
}

