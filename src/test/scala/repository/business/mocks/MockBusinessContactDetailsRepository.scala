package repository.business.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.database.DatabaseErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra

import java.time.LocalDateTime
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest

case class MockBusinessContactDetailsRepository(ref: Ref[IO, List[BusinessContactDetails]]) extends BusinessContactDetailsRepositoryAlgebra[IO] {

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = ???


  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetails]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, Int]] =
    ref.modify { (contactDetails: List[BusinessContactDetails]) =>
      val updatedList =
        BusinessContactDetails(
          id = Some(1),
          userId = createBusinessContactDetailsRequest.userId,
          businessId = createBusinessContactDetailsRequest.businessId,
          businessName = Some(createBusinessContactDetailsRequest.businessName),
          primaryContactFirstName = Some(createBusinessContactDetailsRequest.primaryContactFirstName),
          primaryContactLastName = Some(createBusinessContactDetailsRequest.primaryContactLastName),
          contactEmail = Some(createBusinessContactDetailsRequest.contactEmail),
          contactNumber = Some(createBusinessContactDetailsRequest.contactNumber),
          websiteUrl = Some(createBusinessContactDetailsRequest.websiteUrl),
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        ) :: contactDetails
      (updatedList, validNel(1))
    }

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] = ???
}
