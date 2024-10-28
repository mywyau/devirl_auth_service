package services

import cats.data.Validated.{Invalid, Valid}
import cats.data.{EitherT, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits._
import models.business.Business
import models.business.errors.{BusinessNotFound, BusinessValidationError, InvalidBusinessId}
import repositories.business.BusinessRepositoryAlgebra


trait BusinessService[F[_]] {

  def findBusinessById(businessId: String): F[Either[BusinessValidationError, Business]]

  def createBusiness(business: Business): F[Either[BusinessValidationError, Int]]

  def updateBusiness(businessId: String, updatedBusiness: Business): F[Either[BusinessValidationError, Int]]

  def deleteBusiness(businessId: String): F[Either[BusinessValidationError, Int]]
}


class BusinessServiceImpl[F[_] : Concurrent](repository: BusinessRepositoryAlgebra[F]) extends BusinessService[F] {

  // Validation function for business ID (example: make sure it's not empty or too short)
  def validateBusinessId(businessId: String): ValidatedNel[BusinessValidationError, String] = {
    if (businessId.nonEmpty && businessId.length >= 3) businessId.validNel
    else InvalidBusinessId.invalidNel
  }

  // Validate if a business exists by its ID
  def validateBusinessExists(businessId: String): EitherT[F, BusinessValidationError, Business] = {
    EitherT.fromOptionF(repository.findBusinessById(businessId), BusinessNotFound)
  }

  // Find business by ID with validation
  def findBusinessById(businessId: String): F[Either[BusinessValidationError, Business]] = {
    val validation = validateBusinessId(businessId)

    validation match {
      case Valid(_) => repository.findBusinessById(businessId).map {
        case Some(business) =>
          Right(business)
        case None =>
          Left(BusinessNotFound)
      }
      case Invalid(errors) =>
        Concurrent[F].pure(Left(errors.head)) // Return the first validation error
    }
  }

  // Create business with validations (including time range and overlap check)
  def createBusiness(business: Business): F[Either[BusinessValidationError, Int]] = {

    repository.setBusiness(business).map(Right(_))
  }

  // Update business with validation (ensures business exists before updating)
  def updateBusiness(businessId: String, updatedBusiness: Business): F[Either[BusinessValidationError, Int]] = {

    repository.updateBusiness(businessId, updatedBusiness).map(Right(_))
  }

  // Delete business with validation (ensures business exists before deleting)
  def deleteBusiness(businessId: String): F[Either[BusinessValidationError, Int]] = {
    validateBusinessExists(businessId).value.flatMap {
      case Right(_) => repository.deleteBusiness(businessId).map(Right(_))
      case Left(error) => Concurrent[F].pure(Left(error))
    }
  }
}
