package services.desk

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.*
import models.desk.deskSpecifications.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.DeskType
import repositories.desk.DeskSpecificationsRepositoryAlgebra

trait DeskSpecificationsServiceAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[DeskSpecificationsPartial]]

  def findByOfficeId(officeId: String): F[List[DeskSpecificationsPartial]]

  def create(request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(deskId: String, request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class DeskSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  deskSpecificationsRepo: DeskSpecificationsRepositoryAlgebra[F]
) extends DeskSpecificationsServiceAlgebra[F] {

  override def findByDeskId(deskId: String): F[Option[DeskSpecificationsPartial]] =
    deskSpecificationsRepo.findByDeskId(deskId)

  override def findByOfficeId(officeId: String): F[List[DeskSpecificationsPartial]] =
    deskSpecificationsRepo.findByOfficeId(officeId)

  override def create(request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskSpecificationsRepo.create(request)

  override def update(deskId: String, request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskSpecificationsRepo.update(deskId, request)

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskSpecificationsRepo.delete(deskId)

  override def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskSpecificationsRepo.deleteAllByOfficeId(officeId)
}

object DeskSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad](deskSpecificationsRepo: DeskSpecificationsRepositoryAlgebra[F]): DeskSpecificationsServiceImpl[F] =
    new DeskSpecificationsServiceImpl[F](deskSpecificationsRepo)
}
