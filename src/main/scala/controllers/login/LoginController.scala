package controllers.login

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.users.login.errors.LoginErrorResponse
import models.users.login.requests.UserLoginRequest
import models.users.wanderer_profile.responses.LoginResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.login.LoginServiceAlgebra


trait LoginController[F[_]] {
  def routes: HttpRoutes[F]
}

object LoginController {
  def apply[F[_] : Concurrent](
                                loginService: LoginServiceAlgebra[F]
                              ): LoginController[F] =
    new LoginControllerImpl[F](loginService)
}

class LoginControllerImpl[F[_] : Concurrent](
                                              loginService: LoginServiceAlgebra[F]
                                            ) extends Http4sDsl[F] with LoginController[F] {

  implicit val userLoginDecoder: EntityDecoder[F, UserLoginRequest] = jsonOf[F, UserLoginRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    // Login an existing user
    case req@POST -> Root / "login" =>
      req.decode[UserLoginRequest] { request =>
        loginService.loginUser(request).flatMap {
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
              LoginErrorResponse(
                List("Invalid username"),
                List("Invalid password")
              ).asJson) // TODO: Fix and change to Unauthorzied and return all validation errors
        }
      }
  }
}
