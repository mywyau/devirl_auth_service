package controllers

//import cats.effect.unsafe.implicits.global // for debugging and IO printlns

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.auth.{RegisterEmailErrors, RegisterPasswordErrors, RegisterUsernameErrors}
import models.users.wanderer_profile.requests.UserSignUpRequest
import models.users.wanderer_profile.responses.CreatedUserResponse
import models.users.wanderer_profile.responses.error.RegistrationErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.auth.algebra.*


trait WandererProfileController[F[_]] {
  def routes: HttpRoutes[F]
}

object WandererProfileController {
  def apply[F[_] : Concurrent](authService: AuthenticationServiceAlgebra[F],
                               registrationService: RegistrationServiceAlgebra[F],
                              ): WandererProfileController[F] =
    new WandererProfileControllerImpl[F](authService, registrationService)
}

class WandererProfileControllerImpl[F[_] : Concurrent](
                                                        authService: AuthenticationServiceAlgebra[F],
                                                        registrationService: RegistrationServiceAlgebra[F],
                                                      ) extends Http4sDsl[F] with WandererProfileController[F] {

  implicit val userSignUpRequestDecoder: EntityDecoder[F, UserSignUpRequest] = jsonOf[F, UserSignUpRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req@POST -> Root / "wanderer" / "user" / "profile" / "all-data" =>
      //      IO(println("Received a POST /register request")).unsafeRunSync() // Print immediately
      req.decode[UserSignUpRequest] { request =>
        //        IO(println(s"Received UserSignUpRequest: $request")).unsafeRunSync() // Log incoming payload
        registrationService.registerUser(request).flatMap {
          case Valid(user) =>
            Created(CreatedUserResponse("User created successfully").asJson)
          case Invalid(errors) => {

            val passwordErrors =
              errors.collect {
                case e: RegisterPasswordErrors => e.errorMessage
              }

            val usernameErrors =
              errors.collect {
                case e: RegisterUsernameErrors => e.errorMessage
              }

            val emailErrors =
              errors.collect {
                case e: RegisterEmailErrors => e.errorMessage
              }

            // Create the structured error response
            val errorResponse =
              RegistrationErrorResponse(
                usernameErrors = usernameErrors,
                passwordErrors = passwordErrors,
                emailErrors = emailErrors
              )

            BadRequest(errorResponse.asJson)
          }
        }
      }
  }
}
