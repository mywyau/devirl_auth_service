package services.office

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office_listing.requests.InitiateOfficeListingRequest
import models.office_listing.OfficeListing
import models.office_listing.OfficeListingCard
import repositories.office.OfficeListingRepositoryAlgebra

trait OfficeListingServiceAlgebra[F[_]] {

  def findAll(businessId: String): F[List[OfficeListing]]

  def findAllListingCardDetails(businessId: String): F[List[OfficeListingCard]]

  def getByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListingCard]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  officeListingRepository: OfficeListingRepositoryAlgebra[F]
) extends OfficeListingServiceAlgebra[F] {

  override def findAll(businessId: String): F[List[OfficeListing]] =
    officeListingRepository.findAll(businessId)

  override def getByOfficeId(officeId: String): F[Option[OfficeListing]] =
    officeListingRepository.findByOfficeId(officeId)

  override def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListingCard]] =
    for {
      createdListing: ValidatedNel[DatabaseErrors, DatabaseSuccess] <- officeListingRepository.initiate(request)
      foundListing: Option[OfficeListing] <- officeListingRepository.findByOfficeId(request.officeId)
    } yield foundListing.map(details =>
      OfficeListingCard(
        businessId = details.specifications.businessId,
        officeId = details.specifications.officeId,
        officeName = details.specifications.officeName.getOrElse(""),
        description = details.specifications.description.getOrElse("")
      )
    )

  override def findAllListingCardDetails(businessId: String): F[List[OfficeListingCard]] =
    for {
      allListings: List[OfficeListing] <- officeListingRepository.findAll(businessId)
      createCardDetails: List[OfficeListingCard] = allListings.map(details =>
        OfficeListingCard(
          businessId = details.specifications.businessId,
          officeId = details.specifications.officeId,
          officeName = details.specifications.officeName.getOrElse(""),
          description = details.specifications.description.getOrElse("")
        )
      )
    } yield createCardDetails

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeListingRepository.delete(officeId)

  override def deleteByBusinessId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeListingRepository.deleteByBusinessId(officeId)
}

object OfficeListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    officeListingRepository: OfficeListingRepositoryAlgebra[F]
  ): OfficeListingServiceImpl[F] =
    new OfficeListingServiceImpl[F](officeListingRepository)
}
