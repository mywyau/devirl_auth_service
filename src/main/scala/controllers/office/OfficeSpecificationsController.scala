package controllers.office

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.office.specifications.OfficeSpecifications
import models.responses.{CreatedResponse, DeletedResponse, ErrorResponse}
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

  implicit val officeSpecificationsRequestDecoder: EntityDecoder[F, OfficeSpecifications] = jsonOf[F, OfficeSpecifications]

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
      logger.info(s"[OfficeListingControllerImpl] POST - Creating office listing") *>
        req.decode[OfficeSpecifications] { request =>
          officeSpecificationsService.create(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[OfficeListingControllerImpl] POST - Successfully created a office specifications") *>
                Created(CreatedResponse("Office specifications created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "offices" / "specifications" / officeId =>
      logger.info(s"[OfficeAddressControllerImpl] DELETE - Attempting to delete the office specifications") *>
        officeSpecificationsService.delete(officeId).flatMap {
          case Valid(address) =>
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
