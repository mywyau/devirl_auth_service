package controllers.desk_listing

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.effect.IO
import cats.implicits.*
import io.circe.syntax.*
import models.desk_listing.requests.DeskListingRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.desk_listing.DeskListingServiceAlgebra

trait DeskListingController[F[_]] {
  def routes: HttpRoutes[F]
}

class DeskListingControllerImpl[F[_] : Concurrent : Logger](
  deskService: DeskListingServiceAlgebra[F]
) extends DeskListingController[F]
    with Http4sDsl[F] {

  implicit val deskListingRequestDecoder: EntityDecoder[F, DeskListingRequest] = jsonOf[F, DeskListingRequest]

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ GET -> Root / "business" / "desk" / "listing" / "details" / "find" / deskId =>
      Logger[F].info(s"[DeskListingControllerImpl] GET - Attempting to retrieve a desk listing for $deskId") *>
        deskService.findByDeskId(deskId).flatMap {
          case Some(desk) =>
            Logger[F].info(s"[DeskListingControllerImpl] GET - Successfully retrieved a desk listing for $deskId") *>
              Ok(desk.asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case req @ GET -> Root / "business" / "desk" / "listing" / "details" / "find" / "all" / officeId =>
      Logger[F].info(s"[DeskListingControllerImpl] GET - Attempting to retrieve all desk listings for $officeId") *>
        deskService.findByOfficeId(officeId).flatMap {
          case Nil =>
            InternalServerError(ErrorResponse("Code", "An error occurred did not find any desks for given office id").asJson)
          case desks =>
            Logger[F].info(s"[DeskListingControllerImpl] GET - Successfully retrieved all desk listings for $officeId") *>
              Ok(desks.asJson)

        }

    case req @ POST -> Root / "business" / "desk" / "listing" / "details" / "create" =>
      Logger[F].info(s"[DeskListingControllerImpl] POST - Creating desk listing") *>
        req.decode[DeskListingRequest] { request =>
          deskService.create(request).flatMap {
            case Valid(_) =>
              Logger[F].info(s"[DeskListingControllerImpl] POST - Successfully created a desk listing") *>
                Created(CreatedResponse("Business Desk created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "desk" / "listing" / "details" / "update" / deskId =>
      Logger[F].info(s"[DeskListingControllerImpl] POST - Attempting to update desk listing") *>
        req.decode[DeskListingRequest] { request =>
          deskService.update(deskId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[DeskListingControllerImpl] POST - Successfully updating a desk listing") *>
                Ok(UpdatedResponse(response.toString, "desk listing updated successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
          }
        }
    case DELETE -> Root / "business" / "desk" / "listing" / "details" / "delete" / deskId =>
      Logger[F].info(s"[DeskListingControllerImpl] DELETE - Attempting to delete desk listing for desk id: $deskId") *>
        deskService.delete(deskId).flatMap {
          case Valid(_) =>
            Logger[F].info(s"[DeskListingControllerImpl] DELETE - Successfully deleted desk listing for desk id: $deskId") *>
              Ok(DeletedResponse("Successfully deleted desk listing").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case DELETE -> Root / "business" / "desk" / "listing" / "details" / "delete" / "all" / officeId =>
      Logger[F].info(s"[DeskListingControllerImpl] DELETE - Attempting to delete all desk listing for office id: $officeId") *>
        deskService.deleteAllByOfficeId(officeId).flatMap {
          case Valid(_) =>
            Logger[F].info(s"[DeskListingControllerImpl] DELETE - Successfully deleted all desk listings for office id: $officeId") *>
              Ok(DeletedResponse(s"All desk listings deleted successfully for office id: $officeId").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }
  }
}

object DeskListingController {
  def apply[F[_] : Concurrent](deskService: DeskListingServiceAlgebra[F])(implicit logger: Logger[F]): DeskListingController[F] =
    new DeskListingControllerImpl[F](deskService)
}
