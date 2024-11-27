package repository.business.office.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import models.office.office_contact_details.OfficeContactDetails

import java.time.LocalDateTime

case class MockOfficeContactDetailsRepository(ref: Ref[IO, List[OfficeContactDetails]]) extends OfficeContactDetailsRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[OfficeContactDetails]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def createContactDetails(officeContactDetails: OfficeContactDetails): IO[Int] =
    ref.modify(contactDetails => (officeContactDetails :: contactDetails, 1))
}
