package mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.kernel.Ref
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

case class MockBusinessContactDetailsRepository(ref: Ref[IO, List[BusinessContactDetailsPartial]]) extends BusinessContactDetailsRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[BusinessContactDetailsPartial]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ref.modify { (contactDetails: List[BusinessContactDetailsPartial]) =>
      val updatedList =
        BusinessContactDetailsPartial(
          userId = createBusinessContactDetailsRequest.userId,
          businessId = createBusinessContactDetailsRequest.businessId,
          primaryContactFirstName = Some(createBusinessContactDetailsRequest.primaryContactFirstName),
          primaryContactLastName = Some(createBusinessContactDetailsRequest.primaryContactLastName),
          contactEmail = Some(createBusinessContactDetailsRequest.contactEmail),
          contactNumber = Some(createBusinessContactDetailsRequest.contactNumber),
          websiteUrl = Some(createBusinessContactDetailsRequest.websiteUrl)
        ) :: contactDetails
      (updatedList, validNel(CreateSuccess))
    }

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def deleteAllByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
}
