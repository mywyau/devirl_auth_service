package services.business.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.specifications.BusinessSpecifications
import models.database.SqlErrors
import repositories.business.BusinessSpecificationsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockBusinessSpecificationsRepository(
                                            existingBusinessSpecification: Map[String, BusinessSpecifications] = Map.empty
                                          ) extends BusinessSpecificationsRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessSpecifications]] = IO.pure(existingBusinessSpecification)

  override def findByBusinessId(businessId: String): IO[Option[BusinessSpecifications]] = IO.pure(existingBusinessSpecification.get(businessId))

  override def createSpecs(businessSpecifications: BusinessSpecifications): IO[ValidatedNel[SqlErrors, Int]] = IO(Valid(1))

  override def deleteSpecifications(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
}
