package controllers.wanderer_address

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.wanderer.wanderer_address.responses.error.WandererAddressErrorResponse
import models.wanderer.wanderer_address.service.WandererAddress
import models.wanderer.wanderer_profile.responses.CreatedUserResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.wanderer_address.WandererAddressServiceAlgebra


trait WandererAddressControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class WandererAddressControllerImpl[F[_] : Concurrent](
                                                        wandererAddressService: WandererAddressServiceAlgebra[F]
                                                      ) extends Http4sDsl[F] with WandererAddressControllerAlgebra[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "wanderer" / "address" / "details" / userId =>
      wandererAddressService.getAddressDetailsByUserId(userId).flatMap {
        case Right(address) =>
          Ok(address.asJson)
        case Left(error) =>
          val errorResponse = WandererAddressErrorResponse(error.code, error.errorMessage)
          BadRequest(errorResponse.asJson)
      }
  }
}

object WandererAddressController {
  def apply[F[_] : Concurrent](
                                wandererAddressService: WandererAddressServiceAlgebra[F]
                              ): WandererAddressControllerAlgebra[F] =
    new WandererAddressControllerImpl[F](wandererAddressService)
}
