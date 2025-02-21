package controllers.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.business.availability.CreateBusinessDaysRequest
import models.business.availability.CreateBusinessOpeningHoursRequest
import models.business.availability.RetrieveSingleBusinessAvailability
import models.business.availability.UpdateBusinessDaysRequest
import models.business.availability.UpdateBusinessOpeningHoursRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.BusinessAvailabilityServiceAlgebra

trait BusinessAvailabilityControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessAvailabilityControllerImpl[F[_] : Concurrent : Logger](businessAvailabilityService: BusinessAvailabilityServiceAlgebra[F])
    extends Http4sDsl[F]
    with BusinessAvailabilityControllerAlgebra[F] {

  implicit val createDayDecoder: EntityDecoder[F, CreateBusinessDaysRequest] = jsonOf[F, CreateBusinessDaysRequest]
  implicit val updateDayDecoder: EntityDecoder[F, UpdateBusinessDaysRequest] = jsonOf[F, UpdateBusinessDaysRequest]

  implicit val createOpeningHoursDecoder: EntityDecoder[F, CreateBusinessOpeningHoursRequest] = jsonOf[F, CreateBusinessOpeningHoursRequest]
  implicit val updateOpeningHoursDecoder: EntityDecoder[F, UpdateBusinessOpeningHoursRequest] = jsonOf[F, UpdateBusinessOpeningHoursRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "availability" / "all" / businessId =>
      Logger[F].info(s"[BusinessAvailabilityControllerImpl] GET - Business availability details for userId: $businessId") *>
        businessAvailabilityService.findAvailabilityForBusiness(businessId).flatMap {
          case availabilities if availabilities.nonEmpty => // ✅ Correctly match multiple records
            Logger[F].info(s"[BusinessAvailabilityControllerImpl] GET - Successfully retrieved business availability") *>
              Ok(availabilities.asJson)
          case _ =>
            val errorResponse = ErrorResponse("error", "error codes")
            BadRequest(errorResponse.asJson)
        }

    // case req @ POST -> Root / "business" / "availability" / "days" / "create" =>
    //   Logger[F].info(s"[BusinessAvailabilityControllerImpl] POST - Creating business availability") *>
    //     req.decode[CreateBusinessDaysRequest] { request =>
    //       businessAvailabilityService.createAvailability(request).flatMap {
    //         case Valid(response) =>
    //           Logger[F].info(s"[BusinessAvailabilityControllerImpl] POST - Successfully created a business availability") *>
    //             Created(CreatedResponse(response.toString, "Business availability details created successfully").asJson)
    //         case Invalid(_) =>
    //           InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
    //       }
    //     }

    case req @ PUT -> Root / "business" / "availability" / "days" / "update" =>
      Logger[F].info(s"[BusinessAvailabilityControllerImpl] PUT - Updating business availability days") *>
        req.decode[UpdateBusinessDaysRequest] { request =>
          businessAvailabilityService.updateDays(request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[BusinessAvailabilityControllerImpl] PUT - Successfully updated business availability for ID: ${request.businessId}") *>
                Ok(UpdatedResponse(response.toString, "Business availability days updated successfully").asJson)
            case Invalid(errors) =>
              Logger[F].warn(s"[BusinessAvailabilityControllerImpl] PUT - Validation failed for business availability update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
          }
        }

    case req @ PUT -> Root / "business" / "availability" / "opening" / "hours" / "update" / businessId =>
      Logger[F].info(s"[BusinessAvailabilityControllerImpl] PUT - Updating business availability with ID: $businessId") *>
        req.decode[UpdateBusinessOpeningHoursRequest] { request =>
          businessAvailabilityService.updateOpeningHours(businessId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[BusinessAvailabilityControllerImpl] PUT - Successfully updated business availability for ID: $businessId") *>
                Ok(UpdatedResponse(response.toString, "Business availability updated successfully").asJson)
            case Invalid(errors) =>
              Logger[F].warn(s"[BusinessAvailabilityControllerImpl] PUT - Validation failed for business availability update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
          }
        }

    case DELETE -> Root / "business" / "availability" / "delete" / "all" / businessId =>
      Logger[F].info(s"[BusinessAvailabilityControllerImpl] DELETE - Attempting to delete business availability") *>
        businessAvailabilityService.deleteAllAvailability(businessId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[BusinessAvailabilityControllerImpl] DELETE - Successfully deleted business availability for $businessId") *>
              Ok(DeletedResponse(response.toString, "Business availability details deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted business availability message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object BusinessAvailabilityController {
  def apply[F[_] : Concurrent](businessAvailabilityService: BusinessAvailabilityServiceAlgebra[F])(implicit logger: Logger[F]): BusinessAvailabilityControllerAlgebra[F] =
    new BusinessAvailabilityControllerImpl[F](businessAvailabilityService)
}
