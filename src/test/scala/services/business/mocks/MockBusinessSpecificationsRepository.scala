package services.business.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.specifications.BusinessSpecifications
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.database.DatabaseErrors
import repositories.business.BusinessSpecificationsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime
import models.business.specifications.requests.UpdateBusinessSpecificationsRequest

class MockBusinessSpecificationsRepository(
                                            existingBusinessSpecification: Map[String, BusinessSpecifications] = Map.empty
                                          ) extends BusinessSpecificationsRepositoryAlgebra[IO] {


  override def update(businessId: String, request: UpdateBusinessSpecificationsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = ???

  def showAllUsers: IO[Map[String, BusinessSpecifications]] = IO.pure(existingBusinessSpecification)

  override def findByBusinessId(businessId: String): IO[Option[BusinessSpecifications]] = IO.pure(existingBusinessSpecification.get(businessId))

  override def create(createBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = IO(Valid(1))

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] = ???
}
