package controllers.office_listing

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.office_listing.OfficeListingServiceAlgebra

trait OfficeListingControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeListingControllerImpl[F[_] : Concurrent](officeListingService: OfficeListingServiceAlgebra[F])(implicit logger: Logger[F]) extends Http4sDsl[F] with OfficeListingControllerAlgebra[F] {

  implicit val initiateOfficeListingRequestDecoder: EntityDecoder[F, InitiateOfficeListingRequest] = jsonOf[F, InitiateOfficeListingRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ POST -> Root / "business" / "office" / "listing" / "initiate" =>
      logger.info(s"[OfficeListingControllerImpl] POST - Initiating office listing") *>
        req.decode[InitiateOfficeListingRequest] { request =>
          officeListingService.initiate(request).flatMap {
            case Some(officeListingCard) =>
              logger.info(s"[OfficeListingControllerImpl] POST - Successfully created an initial office listing") *>
                Created(officeListingCard.asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "CreateFailure", message = "Could not create OfficeListingCard").asJson)
          }
        }

    case GET -> Root / "business" / "office" / "listing" / "find" / officeId =>
      logger.info(s"[OfficeListingControllerImpl] GET - Find office listing: $officeId") *>
        officeListingService.getByOfficeId(officeId).flatMap {
          case None =>
            BadRequest(ErrorResponse(code = "GetFailure", message = "Could not find the office listing").asJson)
          case Some(listing) =>
            logger.info(s"[OfficeListingControllerImpl] GET - Successfully retrieved the office listing: $officeId") *>
              Ok(listing.asJson)
        }

    case GET -> Root / "business" / "office" / "listing" / "find" / "all" / businessId =>
      logger.info(s"[OfficeListingControllerImpl] GET - Find all office listings") *>
        officeListingService.findAll(businessId).flatMap {
          case Nil =>
            BadRequest(ErrorResponse(code = "GetFailure", message = "Could not find any office listings").asJson)
          case listings =>
            logger.info(s"[OfficeListingControllerImpl] GET - Successfully retrieved all office listings") *>
              Ok(listings.asJson)
        }

    case GET -> Root / "business" / "office" / "listing" / "cards" / "find" / "all" / businessId =>
      logger.info(s"[OfficeListingControllerImpl] GET - Find all office listing card details") *>
        officeListingService.findAllListingCardDetails(businessId).flatMap {
          case Nil =>
            // Return an empty JSON list instead of a BadRequest error
            logger.info(s"[OfficeListingControllerImpl] GET - No office listing card details found, returning empty list") *>
              Ok(List.empty.asJson)
          case listingCards =>
            logger.info(s"[OfficeListingControllerImpl] GET - Successfully retrieved all office listing card details") *>
              Ok(listingCards.asJson)
        }

    case DELETE -> Root / "business" / "office" / "listing" / "delete" / officeId =>
      logger.info(s"[OfficeListingControllerImpl] DELETE - Attempting to delete the office listing for officeId: ${officeId}") *>
        officeListingService.delete(officeId).flatMap {
          case Valid(result) =>
            logger.info(s"[OfficeListingControllerImpl] DELETE - Successfully deleted office listing for $officeId") *>
              Ok(DeletedResponse("Office listing deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted office listing message")
            BadRequest(errorResponse.asJson)
        }

    case DELETE -> Root / "business" / "office" / "listing" / "delete" / "all" / businessId =>
      logger.info(s"[OfficeListingControllerImpl] DELETE - Attempting to delete all office listings for businessId: ${businessId}") *>
        officeListingService.deleteByBusinessId(businessId).flatMap {
          case Valid(result) =>
            logger.info(s"[OfficeListingControllerImpl] DELETE - Successfully DELETED ALL office listings for $businessId") *>
              Ok(DeletedResponse("All Office listings deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted all office listings message")
            BadRequest(errorResponse.asJson)
        }

    case DELETE -> Root / "business" / "office" / "listing" / "delete" / "all" / businessId =>
      logger.info(s"[OfficeListingControllerImpl] DELETE - Attempting to delete all office listings for businessId: ${businessId}") *>
        officeListingService.deleteByBusinessId(businessId).flatMap {
          case Valid(result) =>
            logger.info(s"[OfficeListingControllerImpl] DELETE - Successfully DELETED ALL office listings for $businessId") *>
              Ok(DeletedResponse("All Office listings deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted all office listings message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object OfficeListingController {
  def apply[F[_] : Concurrent](officeListingService: OfficeListingServiceAlgebra[F])(implicit logger: Logger[F]): OfficeListingControllerAlgebra[F] =
    new OfficeListingControllerImpl[F](officeListingService)
}
