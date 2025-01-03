package controllers.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.business.specifications.requests.UpdateBusinessSpecificationsRequest
import models.business.specifications.BusinessSpecifications
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.specifications.BusinessSpecificationsServiceAlgebra

trait BusinessSpecificationsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessSpecificationsControllerImpl[F[_] : Concurrent](businessSpecificationsService: BusinessSpecificationsServiceAlgebra[F])(implicit logger: Logger[F])
    extends Http4sDsl[F]
    with BusinessSpecificationsControllerAlgebra[F] {

  implicit val createBusinessSpecificationsRequestDecoder: EntityDecoder[F, CreateBusinessSpecificationsRequest] = jsonOf[F, CreateBusinessSpecificationsRequest]
  implicit val updateBusinessSpecificationsRequestDecoder: EntityDecoder[F, UpdateBusinessSpecificationsRequest] = jsonOf[F, UpdateBusinessSpecificationsRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "specifications" / businessId =>
      logger.debug(s"[BusinessSpecificationsControllerImpl] GET - Business specifications for businessId: $businessId") *>
        businessSpecificationsService.getByBusinessId(businessId).flatMap {
          case Right(specifications) =>
            logger.info(s"[BusinessSpecificationsControllerImpl] GET - Successfully retrieved business specification") *>
              Ok(specifications.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req @ POST -> Root / "business" / "businesses" / "specifications" / "create" =>
      logger.info(s"[BusinessSpecificationsControllerImpl] POST - Creating business listing") *>
        req.decode[CreateBusinessSpecificationsRequest] { request =>
          businessSpecificationsService.create(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[BusinessSpecificationsControllerImpl] POST - Successfully created a business specifications") *>
                Created(CreatedResponse("Business specifications created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "businesses" / "specifications" / "update" / businessId =>
      logger.info(s"[BusinessSpecificationsControllerImpl] PUT - Updating business specifications with ID: $businessId") *>
        req.decode[UpdateBusinessSpecificationsRequest] { request =>
          businessSpecificationsService.update(businessId, request).flatMap {
            case Valid(updatedAddress) =>
              logger.info(s"[BusinessSpecificationsControllerImpl] PUT - Successfully updated business specifications for ID: $businessId") *>
                Ok(UpdatedResponse("Update_Success","Business specifications updated successfully").asJson)
            case Invalid(errors) =>
              logger.warn(s"[BusinessSpecificationsControllerImpl] PUT - Validation failed for business specifications update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
            case _ =>
              logger.error(s"[BusinessSpecificationsControllerImpl] PUT - Error updating business specifications for ID: $businessId") *>
                InternalServerError(ErrorResponse(code = "SERVER_ERROR", message = "An error occurred").asJson)
          }
        }

    case DELETE -> Root / "business" / "businesses" / "specifications" / businessId =>
      logger.info(s"[BusinessSpecificationsControllerImpl] DELETE - Attempting to delete the business specifications") *>
        businessSpecificationsService.delete(businessId).flatMap {
          case Valid(address) =>
            logger.info(s"[BusinessSpecificationsControllerImpl] DELETE - Successfully deleted business specifications for $businessId") *>
              Ok(DeletedResponse("Business specifications deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted business specifications message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object BusinessSpecificationsController {
  def apply[F[_] : Concurrent](businessSpecificationsService: BusinessSpecificationsServiceAlgebra[F])(implicit logger: Logger[F]): BusinessSpecificationsControllerAlgebra[F] =
    new BusinessSpecificationsControllerImpl[F](businessSpecificationsService)
}
