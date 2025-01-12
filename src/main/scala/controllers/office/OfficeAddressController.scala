package controllers.office

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.address.OfficeAddressServiceAlgebra

trait OfficeAddressControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeAddressControllerImpl[F[_] : Concurrent : Logger](
  officeAddressService: OfficeAddressServiceAlgebra[F]
) extends Http4sDsl[F]
    with OfficeAddressControllerAlgebra[F] {

  implicit val officeAddressDecoder: EntityDecoder[F, OfficeAddress] = jsonOf[F, OfficeAddress]
  implicit val updateOfficeAddressRequestDecoder: EntityDecoder[F, UpdateOfficeAddressRequest] = jsonOf[F, UpdateOfficeAddressRequest]
  implicit val createOfficeAddressRequestDecoder: EntityDecoder[F, CreateOfficeAddressRequest] = jsonOf[F, CreateOfficeAddressRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "offices" / "address" / "details" / officeId =>
      Logger[F].info(s"[OfficeAddressControllerImpl] GET - Office address for officeId: $officeId") *>
        officeAddressService.findByOfficeId(officeId).flatMap {
          case Right(address) =>
            Logger[F].info(s"[OfficeAddressControllerImpl] GET - Successfully retrieved office address") *>
              Ok(address.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req @ POST -> Root / "business" / "offices" / "address" / "details" / "create" =>
      Logger[F].info(s"[OfficeListingControllerImpl] POST - Creating office listing") *>
        req.decode[CreateOfficeAddressRequest] { request =>
          officeAddressService.create(request).flatMap {
            case Valid(listing) =>
              Logger[F].info(s"[OfficeListingControllerImpl] POST - Successfully created a office address") *>
                Created(CreatedResponse("Office address created successfully").asJson)
            case Invalid(errors) =>
              Logger[F].warn(s"[OfficeListingControllerImpl] POST - Validation failed for office address creation: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "offices" / "address" / "details" / officeId =>
      Logger[F].info(s"[OfficeListingControllerImpl] PUT - Updating office address with ID: $officeId") *>
        req.decode[UpdateOfficeAddressRequest] { request =>
          officeAddressService.update(officeId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[OfficeListingControllerImpl] PUT - Successfully updated office address for ID: $officeId") *>
                Ok(UpdatedResponse(response.toString, "Office address updated successfully").asJson)
            case Invalid(errors) =>
              Logger[F].warn(s"[OfficeListingControllerImpl] PUT - Validation failed for office address update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
            case _ =>
              Logger[F].error(s"[OfficeListingControllerImpl] PUT - Error updating office address for ID: $officeId") *>
                InternalServerError(ErrorResponse(code = "SERVER_ERROR", message = "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "offices" / "address" / "details" / officeId =>
      Logger[F].info(s"[OfficeAddressControllerImpl] DELETE - Attempting to delete the office address") *>
        officeAddressService.delete(officeId).flatMap {
          case Valid(address) =>
            Logger[F].info(s"[OfficeAddressControllerImpl] DELETE - Successfully deleted office address for $officeId") *>
              Ok(DeletedResponse("Office address deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted office address message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object OfficeAddressController {
  def apply[F[_] : Concurrent](officeAddressService: OfficeAddressServiceAlgebra[F])(implicit logger: Logger[F]): OfficeAddressControllerAlgebra[F] =
    new OfficeAddressControllerImpl[F](officeAddressService)
}
