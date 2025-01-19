package controllers.desk

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.effect.IO
import cats.implicits.*
import io.circe.syntax.*
import models.desk.deskPricing.UpdateDeskPricingRequest
import models.responses.*
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.desk.DeskPricingServiceAlgebra

trait DeskPricingController[F[_]] {
  def routes: HttpRoutes[F]
}

class DeskPricingControllerImpl[F[_] : Concurrent : Logger](
  deskPricingService: DeskPricingServiceAlgebra[F]
) extends DeskPricingController[F]
    with Http4sDsl[F] {

  implicit val updateRequestDecoder: EntityDecoder[F, UpdateDeskPricingRequest] = jsonOf[F, UpdateDeskPricingRequest]
  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ GET -> Root / "desk" / "pricing" / "find" / deskId =>
      Logger[F].info(s"[DeskPricingControllerImpl] GET - Attempting to retrieve a desk pricings for $deskId") *>
        deskPricingService.findByDeskId(deskId).flatMap {
          case Some(desk) =>
            Logger[F].info(s"[DeskPricingControllerImpl] GET - Successfully retrieved a desk pricings for $deskId") *>
              Ok(desk.asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case req @ GET -> Root / "desk" / "pricing" / "find" / "all" / officeId =>
      Logger[F].info(s"[DeskPricingControllerImpl] GET - Attempting to retrieve all desk pricings for $officeId") *>
        deskPricingService.findByOfficeId(officeId).flatMap {
          case Nil =>
            Ok(List.empty.asJson)
          case desks =>
            Logger[F].info(s"[DeskPricingControllerImpl] GET - Successfully retrieved all desk pricings for $officeId") *>
              Ok(desks.asJson)
        }

    case req @ PUT -> Root / "desk" / "pricing" / "update" / deskId =>
      Logger[F].info(s"[DeskPricingControllerImpl] POST - Attempting to update desk pricings") *>
        req.decode[UpdateDeskPricingRequest] { request =>
          deskPricingService.update(deskId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[DeskPricingControllerImpl] POST - Successfully updating a desk pricings") *>
                Ok(UpdatedResponse(response.toString, "desk pricings updated successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
          }
        }
    case DELETE -> Root / "desk" / "pricing" / "delete" / deskId =>
      Logger[F].info(s"[DeskPricingControllerImpl] DELETE - Attempting to delete desk pricings for desk id: $deskId") *>
        deskPricingService.delete(deskId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[DeskPricingControllerImpl] DELETE - Successfully deleted desk pricings for desk id: $deskId") *>
              Ok(DeletedResponse(response.toString, "Successfully deleted desk pricings").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case DELETE -> Root / "desk" / "pricing" / "delete" / "all" / officeId =>
      Logger[F].info(s"[DeskPricingControllerImpl] DELETE - Attempting to delete all desk pricings for office id: $officeId") *>
        deskPricingService.deleteAllByOfficeId(officeId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[DeskPricingControllerImpl] DELETE - Successfully deleted all desk pricings for office id: $officeId") *>
              Ok(DeletedResponse(response.toString, s"All desk pricings deleted successfully for office id: $officeId").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }
  }
}

object DeskPricingController {
  def apply[F[_] : Concurrent](deskPricingService: DeskPricingServiceAlgebra[F])(implicit logger: Logger[F]): DeskPricingController[F] =
    new DeskPricingControllerImpl[F](deskPricingService)
}
