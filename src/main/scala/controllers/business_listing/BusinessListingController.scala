package controllers.business_listing

import cats.data.Validated.Valid
import cats.effect.{Concurrent, IO}
import cats.implicits.*
import io.circe.syntax.*
import models.business.business_listing.requests.BusinessListingRequest
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.*
import services.business.business_listing.BusinessListingServiceAlgebra

trait BusinessListingController[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessListingControllerImpl[F[_] : Concurrent](businessService: BusinessListingServiceAlgebra[F])(implicit logger: Logger[F])
  extends BusinessListingController[F] with Http4sDsl[F] {

  implicit val businessListingRequestDecoder: EntityDecoder[F, BusinessListingRequest] = jsonOf[F, BusinessListingRequest]

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "business" / "businesses" / "listing" / "create" =>
      logger.info(s"[BusinessListingControllerImpl] POST - Creating business listing") *>
        req.decode[BusinessListingRequest] { request =>
          businessService.createBusiness(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[BusinessListingControllerImpl] POST - Successfully created a business listing") *>
                Created(CreatedResponse("Business created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }
  }
}

object BusinessListingController {
  def apply[F[_] : Concurrent : Logger](businessService: BusinessListingServiceAlgebra[F]): BusinessListingController[F] =
    new BusinessListingControllerImpl[F](businessService)
}

