package repository.office.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.database.SqlErrors
import models.office.contact_details.OfficeContactDetails
import repositories.office.OfficeContactDetailsRepositoryAlgebra

import java.time.LocalDateTime

case class MockOfficeContactDetailsRepository(ref: Ref[IO, List[OfficeContactDetails]]) extends OfficeContactDetailsRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[OfficeContactDetails]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def createContactDetails(officeContactDetails: OfficeContactDetails): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify { contactDetails =>
      val updatedList = officeContactDetails :: contactDetails
      (updatedList, validNel(1))
    }


}
