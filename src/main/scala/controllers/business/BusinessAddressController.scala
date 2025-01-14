package controllers.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.requests.UpdateBusinessAddressRequest
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.BusinessAddressServiceAlgebra

trait BusinessAddressControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessAddressControllerImpl[F[_] : Concurrent : Logger](businessAddressService: BusinessAddressServiceAlgebra[F]) extends Http4sDsl[F] with BusinessAddressControllerAlgebra[F] {

  implicit val businessAddressRequestRequestDecoder: EntityDecoder[F, CreateBusinessAddressRequest] = jsonOf[F, CreateBusinessAddressRequest]
  implicit val updateBusinessAddressRequestDecoder: EntityDecoder[F, UpdateBusinessAddressRequest] = jsonOf[F, UpdateBusinessAddressRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "address" / "details" / businessId =>
      Logger[F].info(s"[BusinessAddressControllerImpl] GET - Business address details for userId: $businessId") *>
        businessAddressService.getByBusinessId(businessId).flatMap {
          case Right(address) =>
            Logger[F].info(s"[BusinessAddressControllerImpl] GET - Successfully retrieved business address") *>
              Ok(address.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req @ POST -> Root / "business" / "businesses" / "address" / "details" / "create" =>
      Logger[F].info(s"[BusinessAddressControllerImpl] POST - Creating business address") *>
        req.decode[CreateBusinessAddressRequest] { request =>
          businessAddressService.createAddress(request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[BusinessAddressControllerImpl] POST - Successfully created a business address") *>
                Created(CreatedResponse(response.toString, "Business address details created successfully").asJson)
            case Invalid(_) =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "businesses" / "address" / "details" / "update" / businessId =>
      Logger[F].info(s"[BusinessAddressControllerImpl] PUT - Updating business address with ID: $businessId") *>
        req.decode[UpdateBusinessAddressRequest] { request =>
          businessAddressService.update(businessId, request).flatMap {
            case Valid(response) =>
              Logger[F].info(s"[BusinessAddressControllerImpl] PUT - Successfully updated business address for ID: $businessId") *>
                Ok(UpdatedResponse(response.toString, "Business address updated successfully").asJson)
            case Invalid(errors) =>
              Logger[F].warn(s"[BusinessAddressControllerImpl] PUT - Validation failed for business address update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
          }
        }

    case DELETE -> Root / "business" / "businesses" / "address" / "details" / businessId =>
      Logger[F].info(s"[BusinessAddressControllerImpl] DELETE - Attempting to delete business address") *>
        businessAddressService.delete(businessId).flatMap {
          case Valid(response) =>
            Logger[F].info(s"[BusinessAddressControllerImpl] DELETE - Successfully deleted business address for $businessId") *>
              Ok(DeletedResponse(response.toString, "Business address details deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("placeholder error", "some deleted business address message")
            BadRequest(errorResponse.asJson)
        }
  }
}

object BusinessAddressController {
  def apply[F[_] : Concurrent](businessAddressService: BusinessAddressServiceAlgebra[F])(implicit logger: Logger[F]): BusinessAddressControllerAlgebra[F] =
    new BusinessAddressControllerImpl[F](businessAddressService)
}
