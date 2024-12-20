package controllers.business

import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.address_details.requests.BusinessAddressRequest
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.business.address.BusinessAddressServiceAlgebra

trait BusinessAddressControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class BusinessAddressControllerImpl[F[_] : Concurrent](
                                                        businessAddressService: BusinessAddressServiceAlgebra[F]
                                                      )(implicit logger: Logger[F])
  extends Http4sDsl[F] with BusinessAddressControllerAlgebra[F] {

  implicit val businessAddressRequestRequestDecoder: EntityDecoder[F, BusinessAddressRequest] = jsonOf[F, BusinessAddressRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "businesses" / "address" / "details" / businessId =>
      logger.info(s"[BusinessAddressControllerImpl] GET - Business address details for userId: $businessId") *>
        businessAddressService.getByBusinessId(businessId).flatMap {
          case Right(address) =>
            logger.info(s"[BusinessAddressControllerImpl] GET - Successfully retrieved business address") *>
              Ok(address.asJson)
          case Left(error) =>
            val errorResponse = ErrorResponse(error.code, error.errorMessage)
            BadRequest(errorResponse.asJson)
        }

    case req@POST -> Root / "business" / "businesses" / "address" / "details" / "create" =>
      logger.info(s"[BusinessAddressControllerImpl] POST - Creating business address") *>
        req.decode[BusinessAddressRequest] { request =>
          businessAddressService.createAddress(request).flatMap {
            case Valid(listing) =>
              logger.info(s"[BusinessAddressControllerImpl] POST - Successfully created a business address") *>
                Created(CreatedResponse("Business contact details created successfully").asJson)
            case _ =>
              InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
          }
        }
  }
}

object BusinessAddressController {
  def apply[F[_] : Concurrent](
                                businessAddressService: BusinessAddressServiceAlgebra[F]
                              )(implicit logger: Logger[F]): BusinessAddressControllerAlgebra[F] =
    new BusinessAddressControllerImpl[F](businessAddressService)
}
