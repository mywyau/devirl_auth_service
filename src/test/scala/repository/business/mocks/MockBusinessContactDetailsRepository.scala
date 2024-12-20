package repository.business.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.database.SqlErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra

import java.time.LocalDateTime

case class MockBusinessContactDetailsRepository(ref: Ref[IO, List[BusinessContactDetails]]) extends BusinessContactDetailsRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def createContactDetails(businessContactDetails: BusinessContactDetails): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify { contactDetails =>
      val updatedList = businessContactDetails :: contactDetails
      (updatedList, validNel(1))
    }


}
