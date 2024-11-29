package controllers.office_listing

import cats.effect.Concurrent
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.desk_listing.requests.DeskListingRequest
import models.office.office_listing.requests.OfficeListingRequest
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.office_listing.OfficeListingServiceAlgebra
import io.circe.syntax.EncoderOps
import models.responses.CreatedResponse


trait OfficeListingControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeListingControllerImpl[F[_] : Concurrent](
                                                      officeListingService: OfficeListingServiceAlgebra[F]
                                                    )(implicit logger: Logger[F])
  extends Http4sDsl[F] with OfficeListingControllerAlgebra[F] {

  implicit val officeListingRequestDecoder: EntityDecoder[F, OfficeListingRequest] = jsonOf[F, OfficeListingRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "business" / "businesses" / "office" / "listing" / "create" =>
      logger.info(s"[OfficeListingControllerImpl] POST - Creating office listing") *>
        req.decode[OfficeListingRequest] { request =>
          officeListingService.createOffice(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[OfficeListingControllerImpl] POST - Successfully created office for a business") *>
                Created(CreatedResponse("Business Office created successfully").asJson)
            case Invalid(error) =>
              val errorResponse = ErrorResponse("placeholder code", "error message")
              BadRequest(errorResponse.asJson)
          }
        }
  }
}

object OfficeListingController {
  def apply[F[_] : Concurrent](
                                officeListingService: OfficeListingServiceAlgebra[F]
                              )(implicit logger: Logger[F]): OfficeListingControllerAlgebra[F] =
    new OfficeListingControllerImpl[F](officeListingService)
}
