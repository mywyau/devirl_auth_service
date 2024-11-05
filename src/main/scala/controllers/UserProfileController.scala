package controllers

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.users.responses.*
import models.users.{SignUpRequest, UserLoginRequest, UserRegistrationRequest, UserRoleUpdateRequest}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.auth.algebra.*

trait UserProfileController[F[_]] {
  def routes: HttpRoutes[F]
}

object UserProfileController {
  def apply[F[_] : Concurrent](authService: AuthenticationServiceAlgebra[F],
                               registrationService: RegistrationServiceAlgebra[F],
                               tokenService: TokenServiceAlgebra[F]): UserProfileController[F] =
    new UserControllerImpl[F](authService, registrationService, tokenService)
}

class UserControllerImpl[F[_] : Concurrent](
                                             authService: AuthenticationServiceAlgebra[F],
                                             registrationService: RegistrationServiceAlgebra[F],
                                             tokenService: TokenServiceAlgebra[F]
                                           ) extends Http4sDsl[F] with UserProfileController[F] {

  implicit val signUpRequestDecoder: EntityDecoder[F, SignUpRequest] = jsonOf[F, SignUpRequest]
  implicit val registrationDecoder: EntityDecoder[F, UserRegistrationRequest] = jsonOf[F, UserRegistrationRequest]
  implicit val loginDecoder: EntityDecoder[F, UserLoginRequest] = jsonOf[F, UserLoginRequest]
  implicit val roleUpdateDecoder: EntityDecoder[F, UserRoleUpdateRequest] = jsonOf[F, UserRoleUpdateRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    // Register a new user
    case req@POST -> Root / "register" =>
      req.decode[UserRegistrationRequest] { request =>
        registrationService.registerUser(request).flatMap {
          case Valid(user) =>
            Created(LoginResponse("User created successfully").asJson)
          case Invalid(errors) =>
            BadRequest(ErrorUserResponse(errors).asJson)
        }
      }

    // Login an existing user
    case req@POST -> Root / "login" =>
      req.decode[UserLoginRequest] { request =>
        authService.loginUser(request).flatMap {
          case Right(_) => Ok(LoginResponse("User logged in successfully").asJson)
          case _ => BadRequest(ErrorUserResponse(List("Invalid username or password")).asJson) // TODO: Fix and change to Unauthorzied
        }
      }

    // Logout user and invalidate session
    case POST -> Root / "logout" / token =>
      tokenService.invalidateToken(token).flatMap {
        case true => Ok(LoginResponse("User logged out successfully").asJson)
        case false => BadRequest(ErrorUserResponse(List("Invalid token")).asJson)
      }

    // Refresh access token using a refresh token
    case POST -> Root / "refresh" / token =>
      tokenService.refreshAccessToken(token).flatMap {
        case Some(newToken) => Ok(LoginResponse("Token refreshed successfully").asJson)
        case None =>
          BadRequest(ErrorUserResponse(List("Invalid or expired refresh token")).asJson) // TODO: Fix and change to Unauthorzied
      }

    // Update user role (Admin only)
    case req@PUT -> Root / "user" / "role" / userId =>
      req.decode[UserRoleUpdateRequest] { request =>
        authService.updateUserRole(userId, request.userId, request.newRole).flatMap {
          case Right(_) => Ok(LoginResponse("User role updated successfully").asJson)
          case Left(error) => Forbidden(ErrorUserResponse(List(error)).asJson)
        }
      }

    //    // Get user details
    //    case GET -> Root / "user" / userId =>
    //      authService.getUserDetails(userId).flatMap {
    //        case Some(user) => Ok(user.asJson)
    //        case None => NotFound(ErrorUserResponse(List("User not found")).asJson)
    //      }
    //
    //    // Sample protected route requiring specific role (e.g., Admin access only)
    //    case GET -> Root / "admin" / "protected" =>
    //      authService.requireRole("Admin").flatMap {
    //        case true => Ok(LoginResponse("Welcome, Admin!").asJson)
    //        case false => Forbidden(ErrorUserResponse(List("Access denied")).asJson)
    //      }
  }
}
