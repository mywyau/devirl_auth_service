package controllers.wanderer_personal_details

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.responses.ErrorResponse
import models.wanderer.wanderer_personal_details.service.WandererPersonalDetails
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.wanderer_personal_details.WandererPersonalDetailsServiceAlgebra


trait WandererPersonalDetailsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class WandererPersonalDetailsControllerImpl[F[_] : Concurrent](
                                                                wandererPersonalDetailsService: WandererPersonalDetailsServiceAlgebra[F]
                                                              ) extends Http4sDsl[F] with WandererPersonalDetailsControllerAlgebra[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "wanderer" / "personal" / "details" / userId =>
      wandererPersonalDetailsService.getPersonalDetailsByUserId(userId).flatMap {
        case Right(address) =>
          Ok(address.asJson)
        case Left(error) =>
          val errorResponse = ErrorResponse(error.code, error.errorMessage)
          BadRequest(errorResponse.asJson)
      }
  }
}

object WandererPersonalDetailsController {
  def apply[F[_] : Concurrent](
                                wandererPersonalDetailsService: WandererPersonalDetailsServiceAlgebra[F]
                              ): WandererPersonalDetailsControllerAlgebra[F] =
    new WandererPersonalDetailsControllerImpl[F](wandererPersonalDetailsService)
}
