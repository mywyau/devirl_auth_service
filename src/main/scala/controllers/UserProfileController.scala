package controllers

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.users.requests.UserSignUpRequest
import models.users.responses.*
import models.users.{UserLoginRequest, UserRegistrationRequest, UserRoleUpdateRequest}
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

  implicit val userSignUpRequestDecoder: EntityDecoder[F, UserSignUpRequest] = jsonOf[F, UserSignUpRequest]
  implicit val registrationDecoder: EntityDecoder[F, UserRegistrationRequest] = jsonOf[F, UserRegistrationRequest]
  implicit val loginDecoder: EntityDecoder[F, UserLoginRequest] = jsonOf[F, UserLoginRequest]
  implicit val roleUpdateDecoder: EntityDecoder[F, UserRoleUpdateRequest] = jsonOf[F, UserRoleUpdateRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req@POST -> Root / "register" =>
      //      IO(println("Received a POST /register request")).unsafeRunSync() // Print immediately
      req.decode[UserSignUpRequest] { request =>
        //        IO(println(s"Received UserSignUpRequest: $request")).unsafeRunSync() // Log incoming payload
        registrationService.registerUser(request).flatMap {
          case Valid(user) =>
            Created(CreatedUserResponse("User created successfully").asJson)
          case Invalid(errors) =>
            BadRequest(ErrorUserResponse(errors).asJson) // TODO: Fix and change to return all validation errors for fields like password, email, username etc. 
        }
      }

    // Login an existing user
    case req@POST -> Root / "login" =>
      req.decode[UserLoginRequest] { request =>
        authService.loginUser(request).flatMap {
          case Right(userLoginDetails) => Ok(
            LoginResponse(
              userId = userLoginDetails.user_id,
              username = userLoginDetails.username,
              password_hash = userLoginDetails.password_hash,
              email = userLoginDetails.email,
              role = userLoginDetails.role
            ).asJson)
          case _ =>
            BadRequest(
              ErrorUserResponse(
                List("Invalid username or password")
              ).asJson) // TODO: Fix and change to Unauthorzied and return all validation errors
        }
      }

    //    // Logout user and invalidate session
    //    case POST -> Root / "logout" / token =>
    //      tokenService.invalidateToken(token).flatMap {
    //        case true => Ok(LoginResponse("User logged out successfully").asJson)
    //        case false => BadRequest(ErrorUserResponse(List("Invalid token")).asJson)
    //      }

    //    // Refresh access token using a refresh token
    //    case POST -> Root / "refresh" / token =>
    //      tokenService.refreshAccessToken(token).flatMap {
    //        case Some(newToken) => Ok(LoginResponse("Token refreshed successfully").asJson)
    //        case None =>
    //          BadRequest(ErrorUserResponse(List("Invalid or expired refresh token")).asJson) // TODO: Fix and change to Unauthorzied
    //      }

    //    // Update user role (Admin only)
    //    case req@PUT -> Root / "user" / "role" / userId =>
    //      req.decode[UserRoleUpdateRequest] { request =>
    //        authService.updateUserRole(userId, request.userId, request.newRole).flatMap {
    //          case Right(_) => Ok(LoginResponse("User role updated successfully").asJson)
    //          case Left(error) => Forbidden(ErrorUserResponse(List(error)).asJson)
    //        }
    //      }
  }
}
