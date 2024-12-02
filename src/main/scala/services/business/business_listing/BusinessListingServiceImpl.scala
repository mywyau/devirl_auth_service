package services.business.business_listing

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.{Concurrent, IO}
import cats.implicits.*
import cats.syntax.all.*
import cats.{Monad, NonEmptyParallel}
import models.business.business_address.service.BusinessAddress
import models.business.business_address.errors.BusinessAddressErrors
import models.business.business_listing.errors.BusinessListingErrors
import models.business.business_listing.requests.BusinessListingRequest
import models.business.business_listing.{BusinessListing, errors}
import models.database.{SqlErrors, *}
import repositories.business.{BusinessAddressRepositoryAlgebra, BusinessContactDetailsRepositoryAlgebra, BusinessSpecsRepositoryAlgebra}
import services.business.business_listing.BusinessListingServiceAlgebra


class BusinessListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                businessAddressRepo: BusinessAddressRepositoryAlgebra[F],
                                                                                businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F],
                                                                                businessSpecsRepo: BusinessSpecsRepositoryAlgebra[F]
                                                                              ) extends BusinessListingServiceAlgebra[F] {

  override def findByBusinessId(businessId: String): F[Either[BusinessListingErrors, BusinessListing]] = {
    ???
  }

  override def createBusiness(businessListing: BusinessListingRequest): F[ValidatedNel[SqlErrors, Int]] = {

    val addressCreation: F[ValidatedNel[SqlErrors, Int]] =
      businessAddressRepo.createBusinessAddress(businessListing.addressDetails)

    val contactDetailsCreation: F[ValidatedNel[SqlErrors, Int]] =
      businessContactDetailsRepo.createContactDetails(businessListing.contactDetails)

    val specsCreation: F[ValidatedNel[SqlErrors, Int]] =
      businessSpecsRepo.createSpecs(businessListing.businessSpecs)

    // Run the operations in parallel
    (addressCreation, contactDetailsCreation, specsCreation).parMapN {
      case (Validated.Valid(addressId), Validated.Valid(contactId), Validated.Valid(specsId)) =>
        Valid(1) // All operations succeeded; return success indicator
      case (addressResult, contactResult, specsResult) =>
        // Collect errors if any operation fails
        // Combine errors from all operations
        val errors =
          addressResult.toEither.left.toSeq ++
            contactResult.toEither.left.toSeq ++
            specsResult.toEither.left.toSeq
        DatabaseError.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(UnknownError.invalidNel)
    }
  }

}

object BusinessListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessAddressRepo: BusinessAddressRepositoryAlgebra[F],
                                                   businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F],
                                                   businessSpecsRepo: BusinessSpecsRepositoryAlgebra[F]
                                                 ): BusinessListingServiceImpl[F] =
    new BusinessListingServiceImpl[F](businessAddressRepo, businessContactDetailsRepo, businessSpecsRepo)
}

