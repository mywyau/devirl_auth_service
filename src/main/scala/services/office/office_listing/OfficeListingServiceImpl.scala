package services.office.office_listing

import cats.effect.Concurrent
import cats.syntax.all.*
import cats.{Monad, NonEmptyParallel}
import models.office.office_listing.OfficeListing
import models.office.office_listing.requests.InitiateOfficeListingRequest
import org.typelevel.log4cats.Logger
import repositories.office.OfficeListingRepositoryAlgebra

trait OfficeListingServiceAlgebra[F[_]] {

  def findAll(): F[List[OfficeListing]]

  def getByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListing]]
}


class OfficeListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                              officeListingRepository: OfficeListingRepositoryAlgebra[F],
                                                                            ) extends OfficeListingServiceAlgebra[F] {

  override def findAll(): F[List[OfficeListing]] = {
    officeListingRepository.findAll()
  }

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

