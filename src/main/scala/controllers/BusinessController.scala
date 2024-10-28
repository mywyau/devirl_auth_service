package controllers

import cats.effect.Concurrent
import cats.implicits._
import io.circe.syntax._
import models.business.Business
import models.business.errors.{BusinessNotFound, InvalidBusinessId, InvalidTimeRange}
import models.business.responses.{CreatedBusinessResponse, DeleteBusinessResponse, ErrorBusinessResponse, UpdatedBusinessResponse}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import services._

trait BusinessController[F[_]] {
  def routes: HttpRoutes[F]
}

object BusinessController {
  def apply[F[_] : Concurrent](businessService: BusinessService[F]): BusinessController[F] =
    new BusinessControllerImpl[F](businessService)
}

class BusinessControllerImpl[F[_] : Concurrent](businessService: BusinessService[F]) extends BusinessController[F] with Http4sDsl[F] {

  // Create or get JSON decoder/encoder for Business object (if needed)
  implicit val businessDecoder: EntityDecoder[F, Business] = jsonOf[F, Business]

  // Define routes for the Business Controller
  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // Find business by ID
    case GET -> Root / "business" / businessId =>
      businessService.findBusinessById(businessId).flatMap {
        case Right(business) => Ok(business.asJson)
        case Left(BusinessNotFound) => NotFound("Business not found")
        case Left(InvalidBusinessId) => BadRequest("Invalid business ID")
        case _ => InternalServerError("An error occurred")
      }

    // Create a new business
    case req@POST -> Root / "business" =>
      req.decode[Business] { business =>
        businessService.createBusiness(business).flatMap {
          case Right(_) => Created(CreatedBusinessResponse("Business created successfully").asJson)
          case Left(InvalidTimeRange) => BadRequest(ErrorBusinessResponse("Invalid time range").asJson)
          case _ => InternalServerError(ErrorBusinessResponse("An error occurred").asJson)
        }
      }

    // Update an existing business by ID
    case req@PUT -> Root / "business" / businessId =>
      req.decode[Business] { updatedBusiness =>
        businessService.updateBusiness(businessId, updatedBusiness).flatMap {
          case Right(_) => Ok(UpdatedBusinessResponse("Business updated successfully").asJson)
          case Left(BusinessNotFound) => NotFound(ErrorBusinessResponse("Business not found").asJson)
          case Left(InvalidTimeRange) => BadRequest(ErrorBusinessResponse("Invalid time range").asJson)
          case _ => InternalServerError(ErrorBusinessResponse("An error occurred").asJson)
        }
      }

    // Delete a business by ID
    case DELETE -> Root / "business" / businessId =>
      businessService.deleteBusiness(businessId).flatMap {
        case Right(_) => Ok(DeleteBusinessResponse("Business deleted successfully").asJson)
        case Left(BusinessNotFound) => NotFound(ErrorBusinessResponse("Business not found").asJson)
        case _ => InternalServerError(ErrorBusinessResponse("An error occurred").asJson)
      }
  }
}
