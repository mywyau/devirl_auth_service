package services.office.office_listing

import cats.Monad
import cats.NonEmptyParallel
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import models.database.DatabaseErrors
import models.office.office_listing.OfficeListing
import models.office.office_listing.OfficeListingCard
import models.office.office_listing.requests.InitiateOfficeListingRequest
import repositories.office.OfficeListingRepositoryAlgebra

trait OfficeListingServiceAlgebra[F[_]] {

  def findAll(businessId: String): F[List[OfficeListing]]

  def findAllListingCardDetails(businessId: String): F[List[OfficeListingCard]]

  def getByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[Option[OfficeListingCard]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, Int]]

  def deleteByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]
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
      createdListing: ValidatedNel[DatabaseErrors, Int] <- officeListingRepository.initiate(request)
      foundListing: Option[OfficeListing] <- officeListingRepository.findByOfficeId(request.officeId)
    } yield foundListing.map(details =>
      OfficeListingCard(
        businessId = details.officeSpecifications.businessId,
        officeId = details.officeSpecifications.officeId,
        officeName = details.officeSpecifications.officeName.getOrElse(""),
        description = details.officeSpecifications.description.getOrElse("")
      )
    )

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, Int]] =
    officeListingRepository.delete(officeId)

  override def deleteByBusinessId(officeId: String): F[ValidatedNel[DatabaseErrors, Int]] =
    officeListingRepository.delete(officeId)

  override def findAllListingCardDetails(businessId: String): F[List[OfficeListingCard]] =
    for {
      allListings: List[OfficeListing] <- officeListingRepository.findAll(businessId)
      createCardDetails: List[OfficeListingCard] = allListings.map(details =>
        OfficeListingCard(
          businessId = details.officeSpecifications.businessId,
          officeId = details.officeSpecifications.officeId,
          officeName = details.officeSpecifications.officeName.getOrElse(""),
          description = details.officeSpecifications.description.getOrElse("")
        )
      )
    } yield createCardDetails

}

object OfficeListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    officeListingRepository: OfficeListingRepositoryAlgebra[F]
  ): OfficeListingServiceImpl[F] =
    new OfficeListingServiceImpl[F](officeListingRepository)
}
