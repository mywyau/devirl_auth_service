package services.office.office_listing

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.{Monad, NonEmptyParallel}
import models.database.*
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.office.office_listing.{OfficeListing, errors}
import repositories.office.OfficeListingRepositoryAlgebra

trait OfficeListingServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListing]]
}


class OfficeListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                              officeListingRepository: OfficeListingRepositoryAlgebra[F],
                                                                            ) extends OfficeListingServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Option[OfficeListing]] = {
    officeListingRepository.findByOfficeId(officeId)
  }


  override def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListing]] = {

    for {
      createdListing <- officeListingRepository.initiate(request)
      foundListing <- officeListingRepository.findByOfficeId(request.officeId)
    } yield {
      foundListing
    }
  }


}

object OfficeListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeListingRepository: OfficeListingRepositoryAlgebra[F],
                                                 ): OfficeListingServiceImpl[F] =
    new OfficeListingServiceImpl[F](officeListingRepository)
}

