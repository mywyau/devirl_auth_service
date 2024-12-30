package controllers.office_listing

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.office_listing.OfficeListingServiceAlgebra


trait OfficeListingControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeListingControllerImpl[F[_] : Concurrent](
                                                      officeListingService: OfficeListingServiceAlgebra[F]
                                                    )(implicit logger: Logger[F])
  extends Http4sDsl[F] with OfficeListingControllerAlgebra[F] {

  implicit val initiateOfficeListingRequestDecoder: EntityDecoder[F, InitiateOfficeListingRequest] = jsonOf[F, InitiateOfficeListingRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req@POST -> Root / "business" / "office" / "listing" / "initiate" =>
      logger.info(s"[OfficeListingControllerImpl] POST - Initiating office listing") *>
        req.decode[InitiateOfficeListingRequest] { request =>
          officeListingService.initiate(request).flatMap {
            case Some(listing) =>
              logger.info(s"[OfficeListingControllerImpl] POST - Successfully created an initial office listing") *>
                Created(listing.asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case GET -> Root / "business" / "office" / "listing" / "find" / "all" =>
      logger.info(s"[OfficeListingControllerImpl] GET - Find all office listings") *>
        officeListingService.findAll().flatMap {
          case Nil =>
            BadRequest(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          case listOfAddresses =>
            logger.info(s"[OfficeListingControllerImpl] GET - Successfully retrieved all office listings") *>
              Ok(listOfAddresses.asJson)
        }
  }
}

object OfficeListingController {
  def apply[F[_] : Concurrent](
                                officeListingService: OfficeListingServiceAlgebra[F]
                              )(implicit logger: Logger[F]): OfficeListingControllerAlgebra[F] =
    new OfficeListingControllerImpl[F](officeListingService)
}
