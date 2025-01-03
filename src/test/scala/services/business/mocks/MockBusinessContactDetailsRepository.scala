package services.business.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.errors.BusinessContactDetailsNotFound
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.database.DatabaseErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest

class MockBusinessContactDetailsRepository(
                                            existingBusinessContactDetails: Map[String, BusinessContactDetails] = Map.empty
                                          ) extends BusinessContactDetailsRepositoryAlgebra[IO] {


  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = ???

  def showAllUsers: IO[Map[String, BusinessContactDetails]] = IO.pure(existingBusinessContactDetails)

  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] = IO.pure(existingBusinessContactDetails.get(businessId))

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = IO(Valid(1))

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] = ???

}
