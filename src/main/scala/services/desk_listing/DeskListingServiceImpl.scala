package services.desk_listing

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.desk_listing.errors.DatabaseError
import models.desk_listing.errors.DeskListingErrors
import models.desk_listing.errors.DeskListingNotFound
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.DeskListingPartial
import models.desk_listing.DeskType
import repositories.desk.DeskListingRepositoryAlgebra

trait DeskListingServiceAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[DeskListingPartial]]

  def findByOfficeId(officeId: String): F[List[DeskListingPartial]]

  def create(request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(deskId: String, request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] 
}

class DeskListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  deskListingRepo: DeskListingRepositoryAlgebra[F]
) extends DeskListingServiceAlgebra[F] {

  override def findByDeskId(deskId: String): F[Option[DeskListingPartial]] =
    deskListingRepo.findByDeskId(deskId)

  override def findByOfficeId(officeId: String): F[List[DeskListingPartial]] = 
    deskListingRepo.findByOfficeId(officeId)

  override def create(request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.create(request)

  override def update(deskId: String, request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.update(deskId, request)

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.delete(deskId)

  override def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.deleteAllByOfficeId(officeId)
}

object DeskListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad](deskListingRepo: DeskListingRepositoryAlgebra[F]): DeskListingServiceImpl[F] =
    new DeskListingServiceImpl[F](deskListingRepo)
}
