package services.business.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.errors.BusinessContactDetailsNotFound
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.database.SqlErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockBusinessContactDetailsRepository(
                                            existingBusinessContactDetails: Map[String, BusinessContactDetails] = Map.empty
                                          ) extends BusinessContactDetailsRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessContactDetails]] = IO.pure(existingBusinessContactDetails)

  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] = IO.pure(existingBusinessContactDetails.get(businessId))

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): IO[ValidatedNel[SqlErrors, Int]] = IO(Valid(1))

  override def delete(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???

}
