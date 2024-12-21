package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.BusinessContactDetailsRequest
import repository.business.mocks.MockBusinessContactDetailsRepository

import java.time.LocalDateTime

object BusinessContactDetailsConstants {

  def testContactDetails(
                          id: Option[Int],
                          userId: String,
                          businessId: String,
                          business_id: String
                        ): BusinessContactDetails =
    BusinessContactDetails(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = "mikeyCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mikey@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def createMockRepo(initialUsers: List[BusinessContactDetails]): IO[MockBusinessContactDetailsRepository] =
    Ref.of[IO, List[BusinessContactDetails]](initialUsers).map(MockBusinessContactDetailsRepository.apply)

}
