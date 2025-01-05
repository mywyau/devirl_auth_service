package controllers.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.contact_details.BusinessContactDetailsServiceAlgebra

trait BusinessContactDetailsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessContactDetailsControllerImpl[F[_] : Concurrent](businessContactDetailsService: BusinessContactDetailsServiceAlgebra[F])(implicit logger: Logger[F])
    extends Http4sDsl[F]
    with BusinessContactDetailsControllerAlgebra[F] {

  implicit val createBusinessContactDetailsRequestDecoder: EntityDecoder[F, CreateBusinessContactDetailsRequest] = jsonOf[F, CreateBusinessContactDetailsRequest]
  implicit val updateBusinessContactDetailsRequestRequestDecoder: EntityDecoder[F, UpdateBusinessContactDetailsRequest] = jsonOf[F, UpdateBusinessContactDetailsRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "contact" / "details" / businessId =>
      logger.debug(s"[BusinessContactDetailsControllerImpl] GET - Business contactDetails details for businessId: $businessId") *>
        businessContactDetailsService.getByBusinessId(businessId).flatMap {
          case Right(contactDetails) =>
            logger.info(s"[BusinessContactDetailsControllerImpl] GET - Successfully retrieved business contact details") *>
              Ok(contactDetails.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            NotFound(errorResponse.asJson)
        }

    case req @ POST -> Root / "business" / "businesses" / "contact" / "details" / "create" =>
      logger.info(s"[BusinessContactControllerImpl] POST - Creating business listing") *>
        req.decode[CreateBusinessContactDetailsRequest] { request =>
          businessContactDetailsService.create(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[BusinessContactControllerImpl] POST - Successfully created a business contact details") *>
                Created(CreatedResponse("Business contact details created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "businesses" / "contact" / "details" / "update" / businessId =>
      logger.info(s"[BusinessContactDetailsControllerImpl] PUT - Updating business contactDetails with ID: $businessId") *>
        req.decode[UpdateBusinessContactDetailsRequest] { request =>
          businessContactDetailsService.update(businessId, request).flatMap {
            case Valid(updatedContactDetails) =>
              logger.info(s"[BusinessContactDetailsControllerImpl] PUT - Successfully updated business contactDetails for ID: $businessId") *>
                Ok(UpdatedResponse("Update_Success", "Business contactDetails updated successfully").asJson)
            case Invalid(errors) =>
              logger.warn(s"[BusinessContactDetailsControllerImpl] PUT - Validation failed for business contactDetails update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
            case _ =>
              logger.error(s"[BusinessContactDetailsControllerImpl] PUT - Error updating business contactDetails for ID: $businessId") *>
                InternalServerError(ErrorResponse(code = "SERVER_ERROR", message = "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "businesses" / "contact" / "details" / businessId =>
      logger.info(s"[BusinessContactControllerImpl] DELETE - Attempting to delete business contact details") *>
        businessContactDetailsService.delete(businessId).flatMap {
          case Valid(contact) =>
            logger.info(s"[BusinessContactControllerImpl] DELETE - Successfully deleted business contact details for $businessId") *>
              Ok(DeletedResponse("Business contact details deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted business contact details message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object BusinessContactDetailsController {
  def apply[F[_] : Concurrent](businessContactDetailsService: BusinessContactDetailsServiceAlgebra[F])(implicit logger: Logger[F]): BusinessContactDetailsControllerAlgebra[F] =
    new BusinessContactDetailsControllerImpl[F](businessContactDetailsService)
}
