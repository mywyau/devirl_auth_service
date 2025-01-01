package services.office.office_listing

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.{Monad, NonEmptyParallel}
import models.database.SqlErrors
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.office.office_listing.{OfficeListing, OfficeListingCard}
import repositories.office.OfficeListingRepositoryAlgebra

trait OfficeListingServiceAlgebra[F[_]] {

  def findAll(): F[List[OfficeListing]]

  def findAllListingCardDetails(): F[List[OfficeListingCard]]

  def getByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListingCard]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]
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


  override def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListingCard]] = {

    for {
      createdListing: ValidatedNel[SqlErrors, Int] <- officeListingRepository.initiate(request)
      foundListing: Option[OfficeListing] <- officeListingRepository.findByOfficeId(request.officeId)
    } yield {
      foundListing.map(details =>
        OfficeListingCard(
          businessId = details.officeSpecifications.businessId,
          officeId = details.officeSpecifications.officeId,
          officeName = details.officeSpecifications.officeName.getOrElse(""),
          description = details.officeSpecifications.description.getOrElse("")
        )
      )
    }
  }


  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = {
    officeListingRepository.delete(officeId)
  }

  override def findAllListingCardDetails(): F[List[OfficeListingCard]] = {
    for {
      allListings: List[OfficeListing] <- officeListingRepository.findAll()
      createCardDetails: List[OfficeListingCard] = allListings.map(details =>
        OfficeListingCard(
          businessId = details.officeSpecifications.businessId,
          officeId = details.officeSpecifications.officeId,
          officeName = details.officeSpecifications.officeName.getOrElse(""),
          description = details.officeSpecifications.description.getOrElse("")
        )
      )
    } yield {
      createCardDetails
    }
  }

}

object OfficeListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeListingRepository: OfficeListingRepositoryAlgebra[F],
                                                 ): OfficeListingServiceImpl[F] =
    new OfficeListingServiceImpl[F](officeListingRepository)
}

