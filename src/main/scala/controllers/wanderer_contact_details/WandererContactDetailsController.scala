package controllers.wanderer_contact_details

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.users.wanderer_personal_details.responses.error.ContactDetailsErrorResponse
import models.users.wanderer_personal_details.service.WandererContactDetails
import models.users.wanderer_personal_details.responses.success.CreatedContactDetailsResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.wanderer_contact_details.WandererContactDetailsServiceAlgebra


trait WandererContactDetailsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class WandererContactDetailsControllerImpl[F[_] : Concurrent](
                                                        wandererContactDetailsService: WandererContactDetailsServiceAlgebra[F]
                                                      ) extends Http4sDsl[F] with WandererContactDetailsControllerAlgebra[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "wanderer" / "contact" / "details" / userId =>
      wandererContactDetailsService.getContactDetailsByUserId(userId).flatMap {
        case Right(address) =>
          Ok(address.asJson)
        case Left(error) =>
          val errorResponse = ContactDetailsErrorResponse(error.code, error.errorMessage)
          BadRequest(errorResponse.asJson)
      }
  }
}

object WandererContactDetailsController {
  def apply[F[_] : Concurrent](
                                wandererContactDetailsService: WandererContactDetailsServiceAlgebra[F]
                              ): WandererContactDetailsControllerAlgebra[F] =
    new WandererContactDetailsControllerImpl[F](wandererContactDetailsService)
}
