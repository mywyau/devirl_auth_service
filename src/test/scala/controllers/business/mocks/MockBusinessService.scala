package controllers.business.mocks

import cats.effect.IO
import controllers.business.constants.BusinessControllerConstants.sampleBusiness_1
import models.business.Business
import models.business.errors.BusinessValidationError
import services.business.algebra.BusinessServiceAlgebra

class MockBusinessService extends BusinessServiceAlgebra[IO] {

  override def findBusinessById(businessId: String): IO[Either[BusinessValidationError, Business]] =
    IO.pure(Right(sampleBusiness_1))

  override def createBusiness(business: Business): IO[Either[BusinessValidationError, Int]] =
    IO.pure(Right(1))

  override def updateBusiness(businessId: String, business: Business): IO[Either[BusinessValidationError, Int]] =
    IO.pure(Right(1))

  override def deleteBusiness(businessId: String): IO[Either[BusinessValidationError, Int]] =
    IO.pure(Right(1))
}