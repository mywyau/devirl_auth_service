package services.business

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.contact_details.errors.*
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessContactDetailsRepositoryAlgebra

trait BusinessContactDetailsServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessContactDetailsErrors, BusinessContactDetailsPartial]]

  def create(businessContactDetails: CreateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, DatabaseSuccess]]

  def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

}

class BusinessContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
) extends BusinessContactDetailsServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Either[BusinessContactDetailsErrors, BusinessContactDetailsPartial]] =
    businessContactDetailsRepo.findByBusinessId(businessId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessContactDetailsNotFound))
    }

  override def create(businessContactDetails: CreateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, DatabaseSuccess]] = {

    val contactDetailsCreation: F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
      businessContactDetailsRepo.create(businessContactDetails)

    contactDetailsCreation
      .map {
        case Validated.Valid(result) =>
          Valid(result)
        case contactDetailsResult =>
          val errors =
            List(contactDetailsResult.toEither.left.getOrElse(Nil))
          BusinessContactDetailsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessContactDetailsDatabaseError.invalidNel)
      }
  }

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, DatabaseSuccess]] = {

    val updateContactDetails: F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
      businessContactDetailsRepo.update(businessId, request)

    updateContactDetails
      .map {
        case Valid(contactDetailsId) =>
          Valid(CreateSuccess)
        case contactDetailsResult =>
          val errors =
            List(contactDetailsResult.toEither.left.getOrElse(Nil))
          BusinessContactDetailsNotFound.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessContactDetailsNotFound.invalidNel)
      }
  }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessContactDetailsRepo.delete(businessId)
}

object BusinessContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
  ): BusinessContactDetailsServiceImpl[F] =
    new BusinessContactDetailsServiceImpl[F](businessContactDetailsRepo)
}
