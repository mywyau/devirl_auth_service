package services.desk

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.*
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskListing.DeskListingCard
import models.office_listing.requests.InitiateOfficeListingRequest
import repositories.desk.DeskListingRepositoryAlgebra

trait DeskListingServiceAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[DeskListing]]

  def findAllListingCardDetails(officeId: String): F[List[DeskListingCard]]

  def initiate(request: InitiateDeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class DeskListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  deskListingRepo: DeskListingRepositoryAlgebra[F]
) extends DeskListingServiceAlgebra[F] {

  override def findByDeskId(deskId: String): F[Option[DeskListing]] =
    deskListingRepo.findByDeskId(deskId)

  override def findAllListingCardDetails(officeId: String): F[List[DeskListingCard]] =
    deskListingRepo.findAll(officeId)

  override def initiate(request: InitiateDeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.initiate(request)

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.delete(deskId)

  override def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    deskListingRepo.deleteAllByOfficeId(officeId)
}

object DeskListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad](deskListingRepo: DeskListingRepositoryAlgebra[F]): DeskListingServiceImpl[F] =
    new DeskListingServiceImpl[F](deskListingRepo)
}
