package repository.business.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.database.SqlErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra

import java.time.LocalDateTime

case class MockBusinessContactDetailsRepository(ref: Ref[IO, List[BusinessContactDetails]]) extends BusinessContactDetailsRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify { (contactDetails: List[BusinessContactDetails]) =>
      val updatedList =
        BusinessContactDetails(
          id = Some(1),
          userId = createBusinessContactDetailsRequest.userId,
          businessId = createBusinessContactDetailsRequest.businessId,
          businessName = createBusinessContactDetailsRequest.businessName,
          primaryContactFirstName = createBusinessContactDetailsRequest.primaryContactFirstName,
          primaryContactLastName = createBusinessContactDetailsRequest.primaryContactLastName,
          contactEmail = createBusinessContactDetailsRequest.contactEmail,
          contactNumber = createBusinessContactDetailsRequest.contactNumber,
          websiteUrl = createBusinessContactDetailsRequest.websiteUrl,
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        ) :: contactDetails
      (updatedList, validNel(1))
    }

  override def delete(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
}
