package services.business.algebra

import cats.data.Validated.{Invalid, Valid}
import cats.data.{EitherT, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits._
import models.business.Business
import models.business.errors.{BusinessNotFound, BusinessValidationError, InvalidBusinessId}
import repositories.business.BusinessRepositoryAlgebra


trait BusinessServiceAlgebra[F[_]] {

  def findBusinessById(businessId: String): F[Either[BusinessValidationError, Business]]

  def createBusiness(business: Business): F[Either[BusinessValidationError, Int]]

  def updateBusiness(businessId: String, updatedBusiness: Business): F[Either[BusinessValidationError, Int]]

  def deleteBusiness(businessId: String): F[Either[BusinessValidationError, Int]]
}
