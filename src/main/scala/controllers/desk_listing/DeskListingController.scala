package controllers.desk_listing

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.business.desk_listing.requests.DeskListingRequest
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.*
import services.business.desk_listing.DeskListingServiceAlgebra

trait DeskListingController[F[_]] {
  def routes: HttpRoutes[F]
}

object DeskListingController {
  def apply[F[_] : Concurrent](deskService: DeskListingServiceAlgebra[F]): DeskListingController[F] =
    new DeskListingControllerImpl[F](deskService)
}

class DeskListingControllerImpl[F[_] : Concurrent](deskService: DeskListingServiceAlgebra[F]) extends DeskListingController[F] with Http4sDsl[F] {

  implicit val businessDeskRequestDecoder: EntityDecoder[F, DeskListingRequest] = jsonOf[F, DeskListingRequest]

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req@POST -> Root / "business" / "desk" / "create" =>
      req.decode[DeskListingRequest] { desk =>
        deskService.createDesk(desk).flatMap {
          case Right(_) => Created(CreatedResponse("Business Desk created successfully").asJson)
          case _ => InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }
      }
  }
}
