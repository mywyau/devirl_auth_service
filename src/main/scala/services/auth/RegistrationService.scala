package services

import cats.data.Validated.{Invalid, Valid}
import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import repositories.UserRepositoryAlgebra

import java.time.LocalDateTime

sealed trait RegistrationValidation

case object NotUnique extends RegistrationValidation

case object UniqueUser extends RegistrationValidation


trait RegistrationService[F[_]] {

  def registerUser(request: UserRegistrationRequest): F[Validated[List[String], User]]
}


class RegistrationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             userRepository: UserRepositoryAlgebra[F],
                                                                             passwordService: PasswordServiceAlgebra[F]
                                                                           ) extends RegistrationService[F] {

  def validateUnique(
                      field: String,
                      value: String,
                      lookup: String => F[Option[User]]
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


  override def registerUser(request: UserRegistrationRequest): F[Validated[List[String], User]] = {

    val passwordValidationF: F[Validated[List[String], String]] =
      Concurrent[F].pure(passwordService.validatePassword(request.password))

    (passwordValidationF, uniqueUser(request)).parMapN { (passwordValid, uniqueValid) =>
      passwordValid.product(uniqueValid).map(_ => request)
    }.flatMap {
      case Valid(request) =>
        passwordService.hashPassword(request.password).flatMap { hashedPassword =>
          val newUser =
            User(
              userId = request.userId,
              username = request.username,
              password_hash = hashedPassword,
              first_name = request.first_name,
              last_name = request.last_name,
              contact_number = request.contact_number,
              email = request.email,
              role = request.role,
              created_at = LocalDateTime.now()
            )
          userRepository.createUser(newUser).map { rows =>
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

object RegistrationService {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userRepository: UserRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): RegistrationService[F] =
    new RegistrationServiceImpl[F](userRepository, passwordService)
}

