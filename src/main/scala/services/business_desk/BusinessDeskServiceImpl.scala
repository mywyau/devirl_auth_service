package services.business_desk

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.business_desk.errors.*
import models.business.business_desk.requests.BusinessDeskRequest
import models.business.business_desk.service.BusinessDesk
import repositories.business.BusinessDeskRepositoryAlgebra


class BusinessDeskServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             businessDeskRepo: BusinessDeskRepositoryAlgebra[F]
                                                                           ) extends BusinessDeskServiceAlgebra[F] {

  override def findByUserId(userId: String): F[Either[BusinessDeskErrors, BusinessDesk]] = {
    businessDeskRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessDeskNotFound))
    }
  }

  override def createDesk(businessDesk: BusinessDeskRequest): F[Either[BusinessDeskErrors, Int]] = {
    businessDeskRepo.createDeskToRent(businessDesk).attempt.flatMap {
      case Right(id) =>
        if (id > 0) {
          Concurrent[F].pure(Right(id))
        } else {
          Concurrent[F].pure(Left(DatabaseError))
        }
      case Left(ex) =>
        Concurrent[F].pure(Left(DatabaseError))
    }
  }

}

object BusinessDeskService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessDeskRepo: BusinessDeskRepositoryAlgebra[F]
                                                 ): BusinessDeskServiceImpl[F] =
    new BusinessDeskServiceImpl[F](businessDeskRepo)
}

