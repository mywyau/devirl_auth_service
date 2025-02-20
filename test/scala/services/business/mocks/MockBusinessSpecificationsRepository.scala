package services.business.mocks

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessSpecificationsPartial
import models.business.specifications.CreateBusinessSpecificationsRequest
import models.business.specifications.UpdateBusinessSpecificationsRequest
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessSpecificationsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockBusinessSpecificationsRepository(
  existingBusinessSpecification: Map[String, BusinessSpecificationsPartial] = Map.empty
) extends BusinessSpecificationsRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessSpecificationsPartial]] = IO.pure(existingBusinessSpecification)

  override def findByBusinessId(businessId: String): IO[Option[BusinessSpecificationsPartial]] = IO.pure(existingBusinessSpecification.get(businessId))

  override def update(businessId: String, request: UpdateBusinessSpecificationsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def create(createBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(CreateSuccess))

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def deleteAllByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
}
