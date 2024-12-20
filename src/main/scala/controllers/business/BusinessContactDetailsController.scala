package controllers.business

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.contact_details.BusinessContactDetails
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.contact_details.BusinessContactDetailsServiceAlgebra

trait BusinessContactDetailsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessContactDetailsControllerImpl[F[_] : Concurrent](
                                                               businessContactDetailsService: BusinessContactDetailsServiceAlgebra[F]
                                                             )(implicit logger: Logger[F])
  extends Http4sDsl[F] with BusinessContactDetailsControllerAlgebra[F] {

  implicit val businessContactDetailsRequestDecoder: EntityDecoder[F, BusinessContactDetails] = jsonOf[F, BusinessContactDetails]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "contact" / "details" / businessId =>
      logger.debug(s"[BusinessContactDetailsControllerImpl] GET - Business contactDetails details for businessId: $businessId") *>
        businessContactDetailsService.getContactDetailsByBusinessId(businessId).flatMap {
          case Right(contactDetails) =>
            logger.info(s"[BusinessContactDetailsControllerImpl] GET - Successfully retrieved business contact details") *>
              Ok(contactDetails.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req@POST -> Root / "business" / "businesses" / "contact" /  "details" / "create" =>
      logger.info(s"[BusinessListingControllerImpl] POST - Creating business listing") *>
        req.decode[BusinessContactDetails] { request =>
          businessContactDetailsService.createBusinessContactDetails(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[BusinessListingControllerImpl] POST - Successfully created a business contact details") *>
                Created(CreatedResponse("Business contact details created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }
  }
}

object BusinessContactDetailsController {
  def apply[F[_] : Concurrent](
                                businessContactDetailsService: BusinessContactDetailsServiceAlgebra[F]
                              )(implicit logger: Logger[F]): BusinessContactDetailsControllerAlgebra[F] =
    new BusinessContactDetailsControllerImpl[F](businessContactDetailsService)
}
