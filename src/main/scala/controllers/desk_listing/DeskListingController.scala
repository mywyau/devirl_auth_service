package controllers.desk_listing

import cats.effect.{Concurrent, IO}
import cats.implicits.*
import io.circe.syntax.*
import models.business.desk_listing.requests.DeskListingRequest
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.*
import services.business.desk_listing.DeskListingServiceAlgebra

trait DeskListingController[F[_]] {
  def routes: HttpRoutes[F]
}

class DeskListingControllerImpl[F[_] : Concurrent](deskService: DeskListingServiceAlgebra[F])(implicit logger: Logger[F])
  extends DeskListingController[F] with Http4sDsl[F] {

  implicit val deskListingRequestDecoder: EntityDecoder[F, DeskListingRequest] = jsonOf[F, DeskListingRequest]

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "business" / "desk" / "listing" / "create" =>
      logger.info(s"[DeskListingControllerImpl] POST - Creating desk listing") *>
        req.decode[DeskListingRequest] { request =>
          deskService.createDesk(request).flatMap {
            case Right(_) =>
              logger.info(s"[DeskListingControllerImpl] POST - Successfully created a desk listing") *>
                Created(CreatedResponse("Business Desk created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
          }
        }
  }
} 

object DeskListingController {
  def apply[F[_] : Concurrent](deskService: DeskListingServiceAlgebra[F])(implicit logger: Logger[F]): DeskListingController[F] =
    new DeskListingControllerImpl[F](deskService)
}

