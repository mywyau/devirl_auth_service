package controllers

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits._
import io.circe.syntax.EncoderOps
import models.users.responses._
import models.users.{UserLoginRequest, UserRegistrationRequest}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import services.AuthenticationService

trait UserController[F[_]] {
  def routes: HttpRoutes[F]
}

object UserController {
  def apply[F[_] : Concurrent](userService: AuthenticationService[F]): UserController[F] =
    new UserControllerImpl[F](userService)
}

class UserControllerImpl[F[_] : Concurrent](authService: AuthenticationService[F]) extends Http4sDsl[F] with UserController[F] {

  implicit val registrationDecoder: EntityDecoder[F, UserRegistrationRequest] = jsonOf[F, UserRegistrationRequest]
  implicit val loginDecoder: EntityDecoder[F, UserLoginRequest] = jsonOf[F, UserLoginRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // Register a new user
    case req @ POST -> Root / "register" =>
      req.decode[UserRegistrationRequest] { request =>
        authService.registerUser(request).flatMap {
          case Valid(user) =>
            Created(LoginResponse("User created successfully").asJson)
          case Invalid(errors) =>
            BadRequest(ErrorUserResponse(errors).asJson)
        }
      }

    case req@POST -> Root / "login" =>
      req.decode[UserLoginRequest] { request =>
        authService.loginUser(request).flatMap {
          case Right(_) => Ok(LoginResponse("User created successfully").asJson)
          case _ => InternalServerError(ErrorUserResponse(List("Cannot login an error occurred")).asJson)
        }
      }
  }
}
