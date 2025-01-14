package services.business

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import cats.NonEmptyParallel
import models.business_listing.requests.InitiateBusinessListingRequest
import models.business_listing.BusinessListing
import models.business_listing.BusinessListingCard
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessListingRepositoryAlgebra

trait BusinessListingServiceAlgebra[F[_]] {

  def findAll(): F[List[BusinessListing]]

  def findAllListingCardDetails(): F[List[BusinessListingCard]]

  def getByBusinessId(businessId: String): F[Option[BusinessListing]]

  def initiate(request: InitiateBusinessListingRequest): F[Option[BusinessListingCard]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteByUserId(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessListingRepository: BusinessListingRepositoryAlgebra[F]
) extends BusinessListingServiceAlgebra[F] {

  override def findAll(): F[List[BusinessListing]] =
    businessListingRepository.findAll()

  override def getByBusinessId(businessId: String): F[Option[BusinessListing]] =
    businessListingRepository.findByBusinessId(businessId)

  override def initiate(request: InitiateBusinessListingRequest): F[Option[BusinessListingCard]] =
    for {
      createdListing: ValidatedNel[DatabaseErrors, DatabaseSuccess] <- businessListingRepository.initiate(request)
      foundListing: Option[BusinessListing] <- businessListingRepository.findByBusinessId(request.businessId)
    } yield foundListing.map(details =>
      BusinessListingCard(
        businessId = details.businessId,
        businessName = details.specifications.businessName.getOrElse(""),
        description = details.specifications.description.getOrElse("")
      )
    )

  override def findAllListingCardDetails(): F[List[BusinessListingCard]] =
    for {
      allListings: List[BusinessListing] <- businessListingRepository.findAll()
      createCardDetails: List[BusinessListingCard] =
        allListings.map(details =>
          BusinessListingCard(
            businessId = details.businessId,
            businessName = details.specifications.businessName.getOrElse(""),
            description = details.specifications.description.getOrElse("")
          )
        )
    } yield createCardDetails

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessListingRepository.delete(businessId)

  override def deleteByUserId(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessListingRepository.deleteByUserId(userId)
}

object BusinessListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessListingRepository: BusinessListingRepositoryAlgebra[F]
  ): BusinessListingServiceImpl[F] =
    new BusinessListingServiceImpl[F](businessListingRepository)
}
