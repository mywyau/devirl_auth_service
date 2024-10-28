package services

import cats.data.*
import cats.data.Validated.{Invalid, Valid}
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


  def uniqueUser(request: UserRegistrationRequest): F[Validated[List[String], RegistrationValidation]] = {
      (
        validateUnique("username", request.username, userRepository.findByUsername),
        validateUnique("contact number", request.contact_number, userRepository.findByContactNumber),
        validateUnique("email", request.email, userRepository.findByEmail)
      ).parMapN((_, _, _) => UniqueUser.valid[List[String]])
  }

  override def registerUser(request: UserRegistrationRequest): F[Validated[List[String], User]] = {

    val passwordValidationF: F[Validated[List[String], String]] =
      Concurrent[F].pure(passwordService.validatePassword(request.password))

    val uniqueFieldValidations: F[Validated[List[String], RegistrationValidation]] =
      (
        validateUnique("username", request.username, userRepository.findByUsername),
        validateUnique("contact number", request.contact_number, userRepository.findByContactNumber),
        validateUnique("email", request.email, userRepository.findByEmail)
      ).parMapN((_, _, _) => UniqueUser.valid[List[String]])

    // Step 3: Combine password and unique validations
    (passwordValidationF, uniqueFieldValidations).parMapN { (passwordValid, uniqueValid) =>
      println(uniqueValid)
      passwordValid.product(uniqueValid).map(_ => request)
    }.flatMap {
      case Valid(request) =>
        println("Here")
        // Step 4: Hash password if validations pass
        passwordService.hashPassword(request.password).flatMap { hashedPassword =>
          val newUser =
            User(
              username = request.username,
              password_hash = hashedPassword,
              first_name = request.first_name,
              last_name = request.last_name,
              contact_number = request.contact_number,
              email = request.email,
              role = request.role,
              created_at = LocalDateTime.now()
            )
          // Step 5: Create user in the repository
          userRepository.createUser(newUser).map { rows =>
            if (rows > 0) Validated.valid(newUser)
            else Validated.invalid(List("Failed to create user."))
          }
        }

      case Invalid(errors) =>
        println("Invalid")
        // Collect all validation errors
        Concurrent[F].pure(Validated.invalid(errors))
    }
  }

  // Validate that a field is unique (username, contact number, email)
  def validateUnique(
                      field: String,
                      value: String,
                      lookup: String => F[Option[User]]
                    ): F[Validated[List[String], RegistrationValidation]] = {
    lookup(value).map {
      case Some(_) => Validated.invalid(List(s"$field already exists"))
      case None => Validated.valid(UniqueUser)
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

