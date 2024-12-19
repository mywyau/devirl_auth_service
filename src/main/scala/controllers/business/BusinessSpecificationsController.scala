package controllers.business

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.specifications.BusinessSpecifications
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.specifications.BusinessSpecificationsServiceAlgebra

trait BusinessSpecificationsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessSpecificationsControllerImpl[F[_] : Concurrent](
                                                               businessSpecificationsService: BusinessSpecificationsServiceAlgebra[F]
                                                             )(implicit logger: Logger[F])
  extends Http4sDsl[F] with BusinessSpecificationsControllerAlgebra[F] {

  implicit val businessSpecificationsRequestDecoder: EntityDecoder[F, BusinessSpecifications] = jsonOf[F, BusinessSpecifications]

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

    case req@POST -> Root / "business" / "businesses" / "specifications" / "create" =>
      logger.info(s"[BusinessListingControllerImpl] POST - Creating business listing") *>
        req.decode[BusinessSpecifications] { request =>
          businessSpecificationsService.create(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[BusinessListingControllerImpl] POST - Successfully created a business specifications") *>
                Created(CreatedResponse("Business specifications created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }
  }
}

object BusinessSpecificationsController {
  def apply[F[_] : Concurrent](
                                businessSpecificationsService: BusinessSpecificationsServiceAlgebra[F]
                              )(implicit logger: Logger[F]): BusinessSpecificationsControllerAlgebra[F] =
    new BusinessSpecificationsControllerImpl[F](businessSpecificationsService)
}
