package repository.office.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.kernel.Ref
import cats.effect.IO
import java.time.LocalDateTime
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetailsPartial
import repositories.office.OfficeContactDetailsRepositoryAlgebra

case class MockOfficeContactDetailsRepository(ref: Ref[IO, List[OfficeContactDetailsPartial]]) extends OfficeContactDetailsRepositoryAlgebra[IO] {

  override def findByOfficeId(officeId: String): IO[Option[OfficeContactDetailsPartial]] =
    ref.get.map(_.find(_.officeId == officeId))

  override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ref.modify { contactDetails =>
      val updatedList: List[OfficeContactDetailsPartial] =
        OfficeContactDetailsPartial(
          businessId = createOfficeContactDetailsRequest.businessId,
          officeId = createOfficeContactDetailsRequest.officeId,
          primaryContactFirstName = Some(createOfficeContactDetailsRequest.primaryContactFirstName),
          primaryContactLastName = Some(createOfficeContactDetailsRequest.primaryContactLastName),
          contactEmail = Some(createOfficeContactDetailsRequest.contactEmail),
          contactNumber = Some(createOfficeContactDetailsRequest.contactNumber)
        ) :: contactDetails

      (updatedList, validNel(CreateSuccess))
    }

  override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def deleteAllByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
}
