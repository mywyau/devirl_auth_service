package services.business

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial
import models.business.contact_details.CreateBusinessContactDetailsRequest
import models.business.contact_details.UpdateBusinessContactDetailsRequest
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessContactDetailsRepositoryAlgebra

trait BusinessContactDetailsServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Option[BusinessContactDetailsPartial]]

  def create(businessContactDetails: CreateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

}

class BusinessContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
) extends BusinessContactDetailsServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Option[BusinessContactDetailsPartial]] =
    businessContactDetailsRepo.findByBusinessId(businessId)

  override def create(businessContactDetails: CreateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessContactDetailsRepo.create(businessContactDetails)

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessContactDetailsRepo.update(businessId, request)

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessContactDetailsRepo.delete(businessId)
}

object BusinessContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
  ): BusinessContactDetailsServiceImpl[F] =
    new BusinessContactDetailsServiceImpl[F](businessContactDetailsRepo)
}
