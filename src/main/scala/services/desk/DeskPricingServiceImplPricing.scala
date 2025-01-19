package services.desk

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.*
import models.desk.deskPricing.UpdateDeskPricingRequest
import models.desk.deskPricing.RetrievedDeskPricing
import repositories.desk.DeskPricingRepositoryAlgebra

trait DeskPricingServiceAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[RetrievedDeskPricing]]

  def findByOfficeId(officeId: String): F[List[RetrievedDeskPricing]]

  def update(deskId: String, request: UpdateDeskPricingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] 
}

class DeskPricingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  deskPricingRepo: DeskPricingRepositoryAlgebra[F]
) extends DeskPricingServiceAlgebra[F] {

  override def findByDeskId(deskId: String): F[Option[RetrievedDeskPricing]] =
    deskPricingRepo.findByDeskId(deskId)

  override def findByOfficeId(officeId: String): F[List[RetrievedDeskPricing]] = 
    deskPricingRepo.findByOfficeId(officeId)

  override def update(deskId: String, request: UpdateDeskPricingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskPricingRepo.update(deskId, request)

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskPricingRepo.delete(deskId)

  override def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskPricingRepo.deleteAllByOfficeId(officeId)
}

object DeskPricingService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad](deskPricingRepo: DeskPricingRepositoryAlgebra[F]): DeskPricingServiceImpl[F] =
    new DeskPricingServiceImpl[F](deskPricingRepo)
}
