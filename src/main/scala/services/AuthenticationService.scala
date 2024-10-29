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

import java.time.LocalDateTime


trait AuthenticationService[F[_]] {

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


class AuthenticationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                               userRepository: UserRepositoryAlgebra[F],
                                                                               passwordService: PasswordServiceAlgebra[F]
                                                                             ) extends AuthenticationService[F] {

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

  // Login user
  override def loginUser(request: UserLoginRequest): F[Either[String, User]] = {
    for {
      hashed_password <- passwordService.hashPassword(request.password)
      result: Either[String, User] <-
        userRepository.findByUsername(request.username).flatMap {
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

  // Validate user token and return authenticated user
  def authUser(token: String): F[Option[User]] = {
    // Replace with actual token validation
    val user = User("username", "hashed_password", "John", "Doe", "07402205071", "john@example.com", Admin, LocalDateTime.of(2025, 1, 1, 0, 0, 0))
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
}


object AuthenticationService {
  def apply[F[_] : Concurrent : NonEmptyParallel](userRepository: UserRepositoryAlgebra[F], passwordService: PasswordServiceAlgebra[F]): AuthenticationService[F] =
    new AuthenticationServiceImpl[F](userRepository, passwordService)
}

