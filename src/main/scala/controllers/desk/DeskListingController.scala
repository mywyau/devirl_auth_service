package controllers.desk

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.effect.IO
import cats.implicits.*
import io.circe.syntax.*
import models.database.CreateSuccess
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.desk.DeskListingServiceAlgebra

trait DeskListingController[F[_]] {
  def routes: HttpRoutes[F]
}

class DeskListingControllerImpl[F[_] : Concurrent : Logger](
  deskService: DeskListingServiceAlgebra[F]
) extends DeskListingController[F]
    with Http4sDsl[F] {

  implicit val initiateRequestDecoder: EntityDecoder[F, InitiateDeskListingRequest] = jsonOf[F, InitiateDeskListingRequest]

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

    case req @ GET -> Root / "desk" / "listing" / "find" / "business-and-office" / "id" / deskId =>
      Logger[F].info(s"[DeskListingControllerImpl] GET - Attempting to retrieve the business and office id for desk listing: $deskId") *>
        deskService.getOfficeAndBusinessIds(deskId).flatMap {
          case Some(deskListingBusinessAndOffice) =>
            Logger[F].info(s"[DeskListingControllerImpl] GET - Successfully retrieved the business and office id for desk listing: $deskId") *>
              Ok(deskListingBusinessAndOffice.asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case GET -> Root / "business" / "desk" / "listing" / "cards" / "find" / "all" / officeId =>
      Logger[F].info(s"[OfficeListingControllerImpl] GET - Find all desk listing card details") *>
        deskService.findAllListingCardDetails(officeId).flatMap {
          case Nil =>
            Logger[F].info(s"[OfficeListingControllerImpl] GET - No desk listing card details found, returning empty list") *>
              Ok(List.empty.asJson)
          case listingCards =>
            Logger[F].info(s"[OfficeListingControllerImpl] GET - Successfully retrieved all desk listing card details") *>
              Ok(listingCards.asJson)
        }

    // Bad we want to paginate results instead
    case GET -> Root / "business" / "desk" / "listing" / "cards" / "stream" / officeId =>
      Logger[F].info(s"[OfficeListingControllerImpl] GET - Stream all desk listing card details for officeId: $officeId") *>
        Ok(
          deskService
            .streamAllListingCardDetails(officeId) // Stream[F, DeskListingCard]
            .map(desk => desk.asJson.noSpaces) // Stream[F, String]
            .intersperse("\n") // Add a newline between JSON objects
            .through(fs2.text.utf8Encode) // Stream[F, Byte]
        )

    case req @ POST -> Root / "business" / "desk" / "listing" / "initiate" =>
      Logger[F].info(s"[DeskListingControllerImpl] POST - Initiating desk listing") *>
        req.decode[InitiateDeskListingRequest] { request =>
          deskService.initiate(request).flatMap {
            case Valid(deskListingCard) =>
              Logger[F].info(s"[DeskListingControllerImpl] POST - Successfully created an initial desk listing") *>
                Created(CreatedResponse(CreateSuccess.toString, "Successfully created an initial desk listing").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "CreateFailure", message = "Could not create DeskListingCard").asJson)
          }
        }

    case DELETE -> Root / "business" / "desk" / "listing" / "details" / "delete" / deskId =>
      Logger[F].info(s"[DeskListingControllerImpl] DELETE - Attempting to delete desk listing for desk id: $deskId") *>
        deskService.delete(deskId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[DeskListingControllerImpl] DELETE - Successfully deleted desk listing for desk id: $deskId") *>
              Ok(DeletedResponse(response.toString, "Successfully deleted desk listing").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }

    case DELETE -> Root / "business" / "desk" / "listing" / "details" / "delete" / "all" / officeId =>
      Logger[F].info(s"[DeskListingControllerImpl] DELETE - Attempting to delete all desk listing for office id: $officeId") *>
        deskService.deleteAllByOfficeId(officeId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[DeskListingControllerImpl] DELETE - Successfully deleted all desk listings for office id: $officeId") *>
              Ok(DeletedResponse(response.toString, s"All desk listings deleted successfully for office id: $officeId").asJson)
          case _ =>
            InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }
  }
}

object DeskListingController {
  def apply[F[_] : Concurrent](deskService: DeskListingServiceAlgebra[F])(implicit logger: Logger[F]): DeskListingController[F] =
    new DeskListingControllerImpl[F](deskService)
}
