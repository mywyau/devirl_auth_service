package services.auth

import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.database.UserLoginDetails
import org.http4s.Request
import org.http4s.server.AuthMiddleware
import org.typelevel.ci.CIStringSyntax
import repositories.users.UserProfileRepositoryAlgebra
import services.auth.algebra.{AuthenticationServiceAlgebra, PasswordServiceAlgebra, UserAuth}

import java.time.LocalDateTime

object UserAuthMiddleware {

  def apply[F[_] : Monad](authService: Kleisli[F, Request[F], Option[UserAuth[F]]]): AuthMiddleware[F, UserAuth[F]] = {
    val authServiceWithOptionT = authService.mapF(opt => OptionT(opt)) // Transforming F[Option[A]] to OptionT[F, A]
    AuthMiddleware(authServiceWithOptionT)
  }
}


class AuthenticationServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                               userRepository: UserProfileRepositoryAlgebra[F],
                                                                               passwordService: PasswordServiceAlgebra[F]
                                                                             ) extends AuthenticationServiceAlgebra[F] {

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
  override def loginUser(request: UserLoginRequest): F[Either[String, UserProfile]] = {
    for {
      hashed_password <- passwordService.hashPassword(request.password)
      result: Either[String, UserProfile] <-
        userRepository.findByUsername(request.username).flatMap {
          case Some(user) if hashed_password == user.userLoginDetails.password_hash =>
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
  def authUser(token: String): F[Option[UserProfile]] = {
    // Replace with actual token validation
    val user =
      UserProfile(
        userId = "user_id_1",
        UserLoginDetails(
          id = Some(1),
          user_id = "user_id_1",
          username = "username",
          password_hash = "hashed_password",
          email = "john@example.com",
          role = Wanderer,
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ),
        first_name = "John",
        last_name = "Doe",
        UserAddress(
          userId = "user_id_1",
          street = "fake street 1",
          city = "fake city 1",
          country = "UK",
          county = Some("County 1"),
          postcode = "CF3 3NJ",
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ),
        contact_number = "07402205071",
        email = "john@example.com",
        role = Admin,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
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

    UserAuthMiddleware(authService)
  }


  override def updateUserRole(requestingUserId: String, targetUserId: String, newRole: Role): F[Either[String, UserProfile]] = {
    for {
      // Fetch the requesting user and target user
      requestingUserOpt <- userRepository.findByUserId(requestingUserId)
      targetUserOpt <- userRepository.findByUserId(targetUserId)

      result <- (requestingUserOpt, targetUserOpt) match {
        // Validate both users exist and that the requester has admin rights
        case (Some(requestingUser), Some(targetUser)) if requestingUser.role == Admin =>
          // Update the target user's role if authorized
          userRepository.updateUserRole(targetUserId, newRole).flatMap {
            case Some(updatedUser) => Concurrent[F].pure(updatedUser.asRight[String])
            case None => Concurrent[F].pure(s"Failed to update role for user with ID $targetUserId".asLeft[UserProfile])
          }

        // Handle cases where the requester is not authorized
        case (Some(requestingUser), _) if requestingUser.role != Admin =>
          Concurrent[F].pure("Unauthorized: Only admins can update user roles".asLeft[UserProfile])

        // Handle cases where either user does not exist
        case (None, _) =>
          Concurrent[F].pure("Requesting user not found".asLeft[UserProfile])
        case (_, None) =>
          Concurrent[F].pure("Target user not found".asLeft[UserProfile])
      }
    } yield result
  }

}


object AuthenticationServiceAlgebra {
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   userRepository: UserProfileRepositoryAlgebra[F],
                                                   passwordService: PasswordServiceAlgebra[F]
                                                 ): AuthenticationServiceAlgebra[F] =
    new AuthenticationServiceImpl[F](userRepository, passwordService)
}

