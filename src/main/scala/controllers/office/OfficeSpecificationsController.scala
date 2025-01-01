package controllers.office

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.responses.{CreatedResponse, DeletedResponse, ErrorResponse, UpdatedResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.OfficeSpecificationsServiceAlgebra

trait OfficeSpecificationsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeSpecificationsControllerImpl[F[_] : Concurrent](
                                                             officeSpecificationsService: OfficeSpecificationsServiceAlgebra[F]
                                                           )(implicit logger: Logger[F])
  extends Http4sDsl[F] with OfficeSpecificationsControllerAlgebra[F] {

  implicit val createOfficeSpecificationsRequestDecoder: EntityDecoder[F, CreateOfficeSpecificationsRequest] = jsonOf[F, CreateOfficeSpecificationsRequest]
  implicit val updateOfficeSpecificationsRequestRequestDecoder: EntityDecoder[F, UpdateOfficeSpecificationsRequest] = jsonOf[F, UpdateOfficeSpecificationsRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "offices" / "specifications" / officeId =>
      logger.debug(s"[OfficeSpecificationsControllerImpl] GET - Office specifications for officeId: $officeId") *>
        officeSpecificationsService.getByOfficeId(officeId).flatMap {
          case Right(specifications) =>
            logger.info(s"[OfficeSpecificationsControllerImpl] GET - Successfully retrieved office specification") *>
              Ok(specifications.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req@POST -> Root / "business" / "offices" / "specifications" / "create" =>
      logger.info(s"[OfficeSpecificationsControllerImpl] POST - Creating office listing") *>
        req.decode[CreateOfficeSpecificationsRequest] { request =>
          officeSpecificationsService.create(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[OfficeSpecificationsControllerImpl] POST - Successfully created a office specifications") *>
                Created(CreatedResponse("Office specifications created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req@PUT -> Root / "business" / "offices" / "specifications" / "update" / officeId =>
      logger.info(s"[OfficeSpecificationsControllerImpl] PUT - Updating office specifications with ID: $officeId") *>
        req.decode[UpdateOfficeSpecificationsRequest] { request =>
          officeSpecificationsService.update(officeId, request).flatMap {
            case Valid(updatedAddress) =>
              logger.info(s"[OfficeSpecificationsControllerImpl] PUT - Successfully updated office specifications for ID: $officeId") *>
                Ok(UpdatedResponse("Office specifications updated successfully").asJson)
            case Invalid(errors) =>
              logger.warn(s"[OfficeSpecificationsControllerImpl] PUT - Validation failed for office specifications update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
            case _ =>
              logger.error(s"[OfficeSpecificationsControllerImpl] PUT - Error updating office specifications for ID: $officeId") *>
                InternalServerError(ErrorResponse(code = "SERVER_ERROR", message = "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "offices" / "specifications" / officeId =>
      logger.info(s"[OfficeAddressControllerImpl] DELETE - Attempting to delete the office specifications") *>
        officeSpecificationsService.delete(officeId).flatMap {
          case Valid(specifications) =>
            logger.info(s"[OfficeAddressControllerImpl] DELETE - Successfully deleted office specifications for $officeId") *>
              Ok(DeletedResponse("Office specifications deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted office specifications message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object OfficeSpecificationsController {
  def apply[F[_] : Concurrent](
                                officeSpecificationsService: OfficeSpecificationsServiceAlgebra[F]
                              )(implicit logger: Logger[F]): OfficeSpecificationsControllerAlgebra[F] =
    new OfficeSpecificationsControllerImpl[F](officeSpecificationsService)
}
