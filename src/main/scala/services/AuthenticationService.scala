package services

import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import org.http4s.Request
import org.http4s.server.AuthMiddleware

import org.typelevel.ci.CIStringSyntax
import repositories.UserRepositoryAlgebra

import org.passay.*
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.Base64


trait AuthenticationService[F[_]] {

  def registerUser(request: UserRegistrationRequest): F[Validated[List[String], User]]

  def loginUser(request: UserLoginRequest): F[Either[String, User]]

  def authUser(token: String): F[Option[User]]

  def authorize(userAuth: UserAuth[F], requiredRole: Role): F[Either[String, UserAuth[F]]]
}

case class UserAuth[F[_]](user: User)

object UserAuthMiddleware {

  def apply[F[_] : Monad](authService: Kleisli[F, Request[F], Option[UserAuth[F]]]): AuthMiddleware[F, UserAuth[F]] = {
    val authServiceWithOptionT = authService.mapF(opt => OptionT(opt)) // Transforming F[Option[A]] to OptionT[F, A]
    AuthMiddleware(authServiceWithOptionT)
  }
}


class AuthenticationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](userRepository: UserRepositoryAlgebra[F]) extends AuthenticationService[F] {

  private val UsernameExistsError = "Username already exists"
  private val ContactNumberExistsError = "Contact number already exists"
  private val EmailExistsError = "Email already exists"
  private val UserCreationError = "Failed to create user due to an unknown error"
  private val InvalidPasswordError = "Password does not meet security requirements"

  // Simple password hashing function using SHA-256
  def hashPassword(password: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(password.getBytes("UTF-8"))
    Base64.getEncoder.encodeToString(hashBytes)
  }

  // Simplified version without Passay rules (you can add the Passay rules if required)
  def validatePassword(password: String): Boolean = password.length >= 8

  // Register a user
  def registerUser(request: UserRegistrationRequest): F[Validated[List[String], User]] = {
    if (!validatePassword(request.password)) {
      return Concurrent[F].pure(Validated.invalid(List(InvalidPasswordError)))
    }

    val hashedPassword = hashPassword(request.password)

    val newUser = User(
      username = request.username,
      password_hash = hashedPassword,
      first_name = request.first_name,
      last_name = request.last_name,
      contact_number = request.contact_number,
      email = request.email,
      role = request.role,
      created_at = LocalDateTime.now()
    )

    // Perform concurrent validations for username, contact number, and email
    val validationResult: F[ValidatedNel[String, Unit]] =
      (validateUnique("username", request.username, userRepository.findByUsername),
        validateUnique("contact number", request.contact_number, userRepository.findByContactNumber),
        validateUnique("email", request.email, userRepository.findByEmail)).parMapN(_ |+| _ |+| _)

    // Create user if validations pass
    validationResult.flatMap {
      case Validated.Valid(_) =>
        userRepository.createUser(newUser).map { rows =>
          if (rows > 0) Validated.valid(newUser)
          else Validated.invalidNel(UserCreationError).leftMap(_.toList)
        }
      case Validated.Invalid(errors) =>
        Concurrent[F].pure(Validated.invalid(errors.toList))
    }
  }

  // Validate that a field is unique (username, contact number, email)
  def validateUnique(field: String, value: String, lookup: String => F[Option[User]]): F[ValidatedNel[String, Unit]] = {
    lookup(value).map {
      case Some(_) => Validated.invalidNel(s"$field already exists")
      case None => Validated.valid(())
    }
  }

  // Login user
  def loginUser(request: UserLoginRequest): F[Either[String, User]] = {
    userRepository.findByUsername(request.username).flatMap {
      case Some(user) if hashPassword(request.password) == user.password_hash =>
        Concurrent[F].pure(Right(user))
      case Some(_) =>
        Concurrent[F].pure(Left("Invalid password"))
      case None =>
        Concurrent[F].pure(Left("Username not found"))
    }
  }

  // Validate user token and return authenticated user
  def authUser(token: String): F[Option[User]] = {
    // Replace with actual token validation
    val user = User("username", "hashed_password", "John", "Doe", "07402205071", "john@example.com", Admin, LocalDateTime.now())
    Concurrent[F].pure(Some(user)) // Replace with actual logic
  }

  // Role-based authorization
  def authorize(userAuth: UserAuth[F], requiredRole: Role): F[Either[String, UserAuth[F]]] = {
    if (userAuth.user.role == requiredRole) Monad[F].pure(Right(userAuth))
    else Monad[F].pure(Left("Forbidden"))
  }

  def authMiddleware: AuthMiddleware[F, UserAuth[F]] = {
    val authService = Kleisli { (req: Request[F]) =>
      val maybeToken: Option[String] = req.headers.get(ci"Authorization").flatMap(_.toList.headOption.map(_.value))

      maybeToken match {
        case Some(token) =>
          // authUser should return F[Option[User]], and then wrap it into F[Option[UserAuth[F]]]
          authUser(token).map {
            case Some(user) => Some(UserAuth[F](user)) // Ensure the type parameter F is used here
            case None => None
          }
        case None =>
          Monad[F].pure(None) // Return F[Option[UserAuth[F]]] for no token case
      }
    }
    
    // Apply the UserAuthMiddleware
    UserAuthMiddleware(authService)
  }


  // Role-based access control middleware
  def rbacMiddleware(requiredRole: Role): AuthMiddleware[F, UserAuth[F]] = {
    val roleCheckService = Kleisli { (req: Request[F]) =>
      val maybeToken = req.headers.get(ci"Authorization").flatMap(_.toList.headOption.map(_.value))

      maybeToken match {
        case Some(token) =>
          authUser(token).flatMap {
            case Some(user) =>
              authorize(UserAuth(user), requiredRole).map {
                case Right(authUser) => Some(authUser)
                case Left(_) => None
              }
            case None => Monad[F].pure(None)
          }
        case None => Monad[F].pure(None)
      }
    }

    UserAuthMiddleware(roleCheckService)
  }

}


object AuthenticationService {
  def apply[F[_] : Concurrent : NonEmptyParallel](userRepository: UserRepositoryAlgebra[F]): AuthenticationService[F] =
    new AuthenticationServiceImpl[F](userRepository)
}

