package controllers.business_address

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.responses.ErrorResponse
import models.users.wanderer_profile.responses.CreatedUserResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.business_address.BusinessAddressServiceAlgebra


trait BusinessAddressControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessAddressControllerImpl[F[_] : Concurrent](
                                                        businessAddressService: BusinessAddressServiceAlgebra[F]
                                                      ) extends Http4sDsl[F] with BusinessAddressControllerAlgebra[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "address" / "details" / userId =>
      businessAddressService.getAddressDetailsByUserId(userId).flatMap {
        case Right(address) =>
          Ok(address.asJson)
        case Left(error) =>
          val errorResponse = ErrorResponse(error.code, error.errorMessage)
          BadRequest(errorResponse.asJson)
      }
  }
}

object BusinessAddressController {
  def apply[F[_] : Concurrent](
                                businessAddressService: BusinessAddressServiceAlgebra[F]
                              ): BusinessAddressControllerAlgebra[F] =
    new BusinessAddressControllerImpl[F](businessAddressService)
}
