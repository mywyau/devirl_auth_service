package controllers.business_desks

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.business.business_desk.requests.BusinessDeskRequest
import models.business.business_desk.service.BusinessDesk
import models.responses.{CreatedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.*
import services.business_desk.BusinessDeskServiceAlgebra

trait BusinessDeskController[F[_]] {
  def routes: HttpRoutes[F]
}

object BusinessDeskController {
  def apply[F[_] : Concurrent](deskService: BusinessDeskServiceAlgebra[F]): BusinessDeskController[F] =
    new BusinessDeskControllerImpl[F](deskService)
}

class BusinessDeskControllerImpl[F[_] : Concurrent](deskService: BusinessDeskServiceAlgebra[F]) extends BusinessDeskController[F] with Http4sDsl[F] {
  
  implicit val businessDeskRequestDecoder: EntityDecoder[F, BusinessDeskRequest] = jsonOf[F, BusinessDeskRequest]
  
  
  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req@POST -> Root / "business" / "desk"/ "create" =>
      req.decode[BusinessDeskRequest] { desk =>
        deskService.createDesk(desk).flatMap {
          case Right(_) => Created(CreatedResponse("BusinessDesk created successfully").asJson)
          case _ => InternalServerError(ErrorResponse("Code", "An error occurred").asJson)
        }
      }
  }
}
