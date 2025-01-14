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
import services.business.BusinessSpecificationsServiceAlgebra

trait BusinessSpecificationsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessSpecificationsControllerImpl[F[_] : Concurrent : Logger](
  businessSpecificationsService: BusinessSpecificationsServiceAlgebra[F]
) extends Http4sDsl[F]
    with BusinessSpecificationsControllerAlgebra[F] {

  implicit val createRequestDecoder: EntityDecoder[F, CreateBusinessSpecificationsRequest] = jsonOf[F, CreateBusinessSpecificationsRequest]
  implicit val updateRequestDecoder: EntityDecoder[F, UpdateBusinessSpecificationsRequest] = jsonOf[F, UpdateBusinessSpecificationsRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "specifications" / businessId =>
      Logger[F].info(s"[BusinessSpecificationsControllerImpl] GET - Business specifications for businessId: $businessId") *>
        businessSpecificationsService.getByBusinessId(businessId).flatMap {
          case Right(specifications) =>
            Logger[F].info(s"[BusinessSpecificationsControllerImpl] GET - Successfully retrieved business specification") *>
              Ok(specifications.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req @ POST -> Root / "business" / "businesses" / "specifications" / "create" =>
      Logger[F].info(s"[BusinessSpecificationsControllerImpl] POST - Creating business listing") *>
        req.decode[CreateBusinessSpecificationsRequest] { request =>
          businessSpecificationsService.create(request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[BusinessSpecificationsControllerImpl] POST - Successfully created a business specifications") *>
                Created(CreatedResponse(response.toString, "Business specifications created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "businesses" / "specifications" / "update" / businessId =>
      Logger[F].info(s"[BusinessSpecificationsControllerImpl] PUT - Updating business specifications with ID: $businessId") *>
        req.decode[UpdateBusinessSpecificationsRequest] { request =>
          businessSpecificationsService.update(businessId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[BusinessSpecificationsControllerImpl] PUT - Successfully updated business specifications for ID: $businessId") *>
                Ok(UpdatedResponse(response.toString, "Business specifications updated successfully").asJson)
            case Invalid(errors) =>
              Logger[F].warn(s"[BusinessSpecificationsControllerImpl] PUT - Validation failed for business specifications update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
          }
        }

    case DELETE -> Root / "business" / "businesses" / "specifications" / businessId =>
      Logger[F].info(s"[BusinessSpecificationsControllerImpl] DELETE - Attempting to delete the business specifications") *>
        businessSpecificationsService.delete(businessId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[BusinessSpecificationsControllerImpl] DELETE - Successfully deleted business specifications for $businessId") *>
              Ok(DeletedResponse(response.toString, "Business specifications deleted successfully").asJson)
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
