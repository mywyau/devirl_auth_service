package services.business.mocks

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime

import models.business.contact_details.CreateBusinessContactDetailsRequest
import models.business.contact_details.UpdateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import weaver.SimpleIOSuite

class MockBusinessContactDetailsRepository(
  existingBusinessContactDetails: Map[String, BusinessContactDetailsPartial] = Map.empty
) extends BusinessContactDetailsRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessContactDetailsPartial]] =
    IO.pure(existingBusinessContactDetails)

  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetailsPartial]] =
    IO.pure(existingBusinessContactDetails.get(businessId))

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    IO(Valid(CreateSuccess))

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ???

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ???

  override def deleteAllByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

}
