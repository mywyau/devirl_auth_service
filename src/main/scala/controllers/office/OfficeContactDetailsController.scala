package controllers.office

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.contact_details.OfficeContactDetailsServiceAlgebra

trait OfficeContactDetailsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeContactDetailsControllerImpl[F[_] : Concurrent](officeContactDetailsService: OfficeContactDetailsServiceAlgebra[F])(implicit logger: Logger[F])
    extends Http4sDsl[F]
    with OfficeContactDetailsControllerAlgebra[F] {

  implicit val createOfficeContactDetailsRequestDecoder: EntityDecoder[F, CreateOfficeContactDetailsRequest] = jsonOf[F, CreateOfficeContactDetailsRequest]
  implicit val updateOfficeContactDetailsRequestDecoder: EntityDecoder[F, UpdateOfficeContactDetailsRequest] = jsonOf[F, UpdateOfficeContactDetailsRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "offices" / "contact" / "details" / officeId =>
      logger.debug(s"[OfficeContactDetailsControllerImpl] GET - Office contact details for officeId: $officeId") *>
        officeContactDetailsService.getByOfficeId(officeId).flatMap {
          case Right(contactDetails) =>
            logger.info(s"[OfficeContactDetailsControllerImpl] GET - Successfully retrieved office specification") *>
              Ok(contactDetails.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req @ POST -> Root / "business" / "offices" / "contact" / "details" / "create" =>
      logger.info(s"[OfficeContactDetailsControllerImpl] POST - Creating office listing") *>
        req.decode[CreateOfficeContactDetailsRequest] { request =>
          officeContactDetailsService.create(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[OfficeContactDetailsControllerImpl] POST - Successfully created a office contact details") *>
                Created(CreatedResponse("Office contact details created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "offices" / "contact" / "details" / "update" / officeId =>
      logger.info(s"[OfficeContactDetailsControllerImpl] PUT - Updating office contact details with ID: $officeId") *>
        req.decode[UpdateOfficeContactDetailsRequest] { request =>
          officeContactDetailsService.update(officeId, request).flatMap {
            case Valid(updatedAddress) =>
              logger.info(s"[OfficeContactDetailsControllerImpl] PUT - Successfully updated office contact details for ID: $officeId") *>
                Ok(UpdatedResponse("Update_Success", "Office contact details updated successfully").asJson)
            case Invalid(errors) =>
              logger.warn(s"[OfficeContactDetailsControllerImpl] PUT - Validation failed for office contact details update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
            case _ =>
              logger.error(s"[OfficeContactDetailsControllerImpl] PUT - Error updating office contact details for ID: $officeId") *>
                InternalServerError(ErrorResponse(code = "SERVER_ERROR", message = "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "offices" / "contact" / "details" / officeId =>
      logger.info(s"[OfficeContactDetailsControllerImpl] DELETE - Attempting to delete the office contact details") *>
        officeContactDetailsService.delete(officeId).flatMap {
          case Valid(contactDetails) =>
            logger.info(s"[OfficeContactDetailsControllerImpl] DELETE - Successfully deleted office contact details for $officeId") *>
              Ok(DeletedResponse("Office contact details deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted office contact details message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object OfficeContactDetailsController {
  def apply[F[_] : Concurrent](officeContactDetailsService: OfficeContactDetailsServiceAlgebra[F])(implicit logger: Logger[F]): OfficeContactDetailsControllerAlgebra[F] =
    new OfficeContactDetailsControllerImpl[F](officeContactDetailsService)
}
