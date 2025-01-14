package controllers.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.business_listing.requests.InitiateBusinessListingRequest
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.BusinessListingServiceAlgebra

trait BusinessListingControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessListingControllerImpl[F[_] : Concurrent : Logger](
  businessListingService: BusinessListingServiceAlgebra[F]
)() extends Http4sDsl[F]
    with BusinessListingControllerAlgebra[F] {

  implicit val initiateBusinessListingRequestDecoder: EntityDecoder[F, InitiateBusinessListingRequest] = jsonOf[F, InitiateBusinessListingRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "listing" / "cards" / "find" / "all" =>
      Logger[F].info(s"[BusinessListingControllerImpl] GET - Find all business listing card details") *>
        businessListingService.findAllListingCardDetails().flatMap {
          case Nil =>
            Ok(List.empty.asJson)
          case listingCards =>
            Logger[F].info(s"[BusinessListingControllerImpl] GET - Successfully retrieved all business listing card details") *>
              Ok(listingCards.asJson)
        }

    case GET -> Root / "business" / "businesses" / "listing" / "find" / businessId =>
      Logger[F].info(s"[BusinessListingControllerImpl] GET - Find business listing: $businessId") *>
        businessListingService.getByBusinessId(businessId).flatMap {
          case None =>
            BadRequest(ErrorResponse(code = "GetFailure", message = "Could not find the business listing").asJson)
          case Some(listing) =>
            Logger[F].info(s"[BusinessListingControllerImpl] GET - Successfully retrieved the business listing: $businessId") *>
              Ok(listing.asJson)
        }

    case req @ POST -> Root / "business" / "businesses" / "listing" / "initiate" =>
      Logger[F].info(s"[BusinessListingControllerImpl] POST - Initiating business listing") *>
        req.decode[InitiateBusinessListingRequest] { request =>
          businessListingService.initiate(request).flatMap {
            case Some(businessListingCard) =>
              Logger[F].info(s"[BusinessListingControllerImpl] POST - Successfully created an initial business listing") *>
                Created(businessListingCard.asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "CreateFailure", message = "Could not create BusinessListingCard").asJson)
          }
        }

    case DELETE -> Root / "business" / "businesses" / "listing" / "delete" / businessId =>
      Logger[F].info(s"[BusinessListingControllerImpl] DELETE - Attempting to delete the business listing for businessId:${businessId}") *>
        businessListingService.delete(businessId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[BusinessListingControllerImpl] DELETE - Successfully deleted business listing for $businessId") *>
              Ok(DeletedResponse(response.toString, "Business listing deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted business listing message")
            BadRequest(errorResponse.asJson)
        }
    case DELETE -> Root / "business" / "businesses" / "listing" / "delete" / "all" / userId =>
      Logger[F].info(s"[BusinessListingControllerImpl] DELETE - Attempting to delete all business listings for userId: ${userId}") *>
        businessListingService.deleteByUserId(userId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[BusinessListingControllerImpl] DELETE - Successfully deleted all business listings for $userId") *>
              Ok(DeletedResponse(response.toString, "All Business listings deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted all business listings message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object BusinessListingController {
  def apply[F[_] : Concurrent](businessListingService: BusinessListingServiceAlgebra[F])(implicit logger: Logger[F]): BusinessListingControllerAlgebra[F] =
    new BusinessListingControllerImpl[F](businessListingService)
}
