package services.business

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.availability.RetrieveSingleBusinessAvailability
import models.business.availability.RetrievedBusinessAvailability
import models.business.availability.UpdateBusinessDaysRequest
import models.business.availability.UpdateBusinessOpeningHoursRequest
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.database.UpdateSuccess
import models.Day
import repositories.business.BusinessAvailabilityRepositoryAlgebra

trait BusinessAvailabilityServiceAlgebra[F[_]] {

  def findAvailabilityForBusiness(businessId: String): F[List[RetrieveSingleBusinessAvailability]]

  def updateDays(request: UpdateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateOpeningHours(businessId: String, request: UpdateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllAvailability(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessAvailabilityServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessAvailabilityRepo: BusinessAvailabilityRepositoryAlgebra[F]
) extends BusinessAvailabilityServiceAlgebra[F] {

  override def findAvailabilityForBusiness(businessId: String): F[List[RetrieveSingleBusinessAvailability]] =
    businessAvailabilityRepo.findAvailabilityForBusiness(businessId)

  override def updateDays(request: UpdateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAvailabilityRepo.updateDaysOpen(request)

  override def updateOpeningHours(businessId: String, request: UpdateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAvailabilityRepo.updateOpeningHours(request)

  override def deleteAllAvailability(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAvailabilityRepo.deleteAll(businessId)
}

object BusinessAvailabilityService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessAvailabilityRepo: BusinessAvailabilityRepositoryAlgebra[F]
  ): BusinessAvailabilityServiceImpl[F] =
    new BusinessAvailabilityServiceImpl[F](businessAvailabilityRepo)
}
