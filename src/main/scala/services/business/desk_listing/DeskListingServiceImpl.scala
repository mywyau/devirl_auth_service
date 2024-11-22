package services.business.desk_listing

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.desk_listing.errors.*
import models.business.desk_listing.requests.DeskListingRequest
import models.business.desk_listing.service.DeskListing
import repositories.business.BusinessDeskRepositoryAlgebra


class BusinessDeskServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                             businessDeskRepo: BusinessDeskRepositoryAlgebra[F]
                                                                           ) extends DeskListingServiceAlgebra[F] {

  override def findByUserId(userId: String): F[Either[BusinessDeskErrors, DeskListing]] = {
    businessDeskRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessDeskNotFound))
    }
  }

  override def createDesk(businessDesk: DeskListingRequest): F[Either[BusinessDeskErrors, Int]] = {
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

