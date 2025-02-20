package controllers.desk

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.effect.IO
import cats.implicits.*
import io.circe.syntax.*
import models.desk.deskSpecifications.UpdateDeskSpecificationsRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.desk.DeskSpecificationsServiceAlgebra

trait DeskSpecificationsController[F[_]] {
  def routes: HttpRoutes[F]
}

class DeskSpecificationsControllerImpl[F[_] : Concurrent : Logger](
  deskService: DeskSpecificationsServiceAlgebra[F]
) extends DeskSpecificationsController[F]
    with Http4sDsl[F] {

  implicit val updateDecoder: EntityDecoder[F, UpdateDeskSpecificationsRequest] = jsonOf[F, UpdateDeskSpecificationsRequest]

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ GET -> Root / "business" / "desk" / "specifications" / "details" / "find" / deskId =>
      Logger[F].info(s"[DeskSpecificationsControllerImpl] GET - Attempting to retrieve a desk specifications for $deskId") *>
        deskService.findByDeskId(deskId).flatMap {
          case Some(desk) =>
            Logger[F].info(s"[DeskSpecificationsControllerImpl] GET - Successfully retrieved a desk specifications for $deskId") *>
              Ok(desk.asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case req @ GET -> Root / "business" / "desk" / "specifications" / "details" / "find" / "all" / officeId =>
      Logger[F].info(s"[DeskSpecificationsControllerImpl] GET - Attempting to retrieve all desk specifications for $officeId") *>
        deskService.findByOfficeId(officeId).flatMap {
          case Nil =>
            BadRequest(ErrorResponse("Code", "An error occurred did not find any desks for given office id").asJson)
          case desks =>
            Logger[F].info(s"[DeskSpecificationsControllerImpl] GET - Successfully retrieved all desk specifications for $officeId") *>
              Ok(desks.asJson)
        }

    case req @ POST -> Root / "business" / "desk" / "specifications" / "details" / "create" =>
      Logger[F].info(s"[DeskSpecificationsControllerImpl] POST - Creating desk specifications") *>
        req.decode[UpdateDeskSpecificationsRequest] { request =>
          deskService.create(request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[DeskSpecificationsControllerImpl] POST - Successfully created a desk specifications") *>
                Created(CreatedResponse(response.toString, "Business Desk created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "desk" / "specifications" / "details" / "update" / deskId =>
      Logger[F].info(s"[DeskSpecificationsControllerImpl] POST - Attempting to update desk specifications") *>
        req.decode[UpdateDeskSpecificationsRequest] { request =>
          deskService.update(deskId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[DeskSpecificationsControllerImpl] POST - Successfully updating a desk specifications") *>
                Ok(UpdatedResponse(response.toString, "desk specifications updated successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "desk" / "specifications" / "details" / "delete" / deskId =>
      Logger[F].info(s"[DeskSpecificationsControllerImpl] DELETE - Attempting to delete desk specifications for desk id: $deskId") *>
        deskService.delete(deskId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[DeskSpecificationsControllerImpl] DELETE - Successfully deleted desk specifications for desk id: $deskId") *>
              Ok(DeletedResponse(response.toString, "Successfully deleted desk specifications").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case DELETE -> Root / "business" / "desk" / "specifications" / "details" / "delete" / "all" / officeId =>
      Logger[F].info(s"[DeskSpecificationsControllerImpl] DELETE - Attempting to delete all desk specifications for office id: $officeId") *>
        deskService.deleteAllByOfficeId(officeId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[DeskSpecificationsControllerImpl] DELETE - Successfully deleted all desk specifications for office id: $officeId") *>
              Ok(DeletedResponse(response.toString, s"All desk specifications deleted successfully for office id: $officeId").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }
  }
}

object DeskSpecificationsController {
  def apply[F[_] : Concurrent](deskService: DeskSpecificationsServiceAlgebra[F])(implicit logger: Logger[F]): DeskSpecificationsController[F] =
    new DeskSpecificationsControllerImpl[F](deskService)
}
