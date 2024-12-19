package services.business.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.business_specs.BusinessSpecifications
import models.database.SqlErrors
import repositories.business.BusinessSpecsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockBusinessSpecificationsRepository(
                                            existingBusinessSpecification: Map[String, BusinessSpecifications] = Map.empty
                                          ) extends BusinessSpecsRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessSpecifications]] = IO.pure(existingBusinessSpecification)

  override def findByBusinessId(businessId: String): IO[Option[BusinessSpecifications]] = IO.pure(existingBusinessSpecification.get(businessId))

  override def createSpecs(businessSpecifications: BusinessSpecifications): IO[ValidatedNel[SqlErrors, Int]] = IO(Valid(1))

}
