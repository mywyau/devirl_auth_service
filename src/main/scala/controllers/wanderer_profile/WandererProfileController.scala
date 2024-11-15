package controllers.wanderer_profile

//import cats.effect.unsafe.implicits.global // for debugging and IO printlns

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.users.wanderer_profile.errors.*
import models.users.wanderer_profile.requests.UserSignUpRequest
import models.users.wanderer_profile.responses.error.{ErrorResponse, WandererProfileErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.wanderer_profile.WandererProfileServiceAlgebra


trait WandererProfileController[F[_]] {
  def routes: HttpRoutes[F]
}

class WandererProfileControllerImpl[F[_] : Concurrent](
                                                        wandererProfileService: WandererProfileServiceAlgebra[F]
                                                      ) extends Http4sDsl[F] with WandererProfileController[F] {

  implicit val userSignUpRequestDecoder: EntityDecoder[F, UserSignUpRequest] = jsonOf[F, UserSignUpRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "wanderer" / "user" / "profile" / user_id =>
      wandererProfileService.createProfile(user_id).flatMap {
        case Valid(userProfile) =>
          Ok(userProfile.asJson)
        case Invalid(errors) =>

          val loginDetailsErrors: List[ErrorResponse] = errors.collect {
            case error: WandererLoginDetailsError => ErrorResponse(error.code, error.message)
          }

          val addressErrors: List[ErrorResponse] = errors.collect {
            case error: WandererAddressError => ErrorResponse(error.code, error.message)
          }

          val contactDetailsErrors: List[ErrorResponse] = errors.collect {
            case error: WandererContactDetailsError => ErrorResponse(error.code, error.message)
          }

          val otherWandererProfileError: List[ErrorResponse] = errors.collect {
            case error: OtherWandererProfileError => ErrorResponse(error.code, error.message)
          }

          val errorResponse: WandererProfileErrorResponse =
            WandererProfileErrorResponse(
              loginDetailsErrors = loginDetailsErrors,
              addressErrors = addressErrors,
              contactDetailsErrors = contactDetailsErrors,
              otherErrors = otherWandererProfileError
            )

          BadRequest(errorResponse.asJson)
      }
  }
}

object WandererProfileController {
  def apply[F[_] : Concurrent](
                                wandererProfileService: WandererProfileServiceAlgebra[F]
                              ): WandererProfileController[F] =
    new WandererProfileControllerImpl[F](wandererProfileService)
}
