package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import repository.business.mocks.MockBusinessContactDetailsRepository

import java.time.LocalDateTime

object BusinessContactDetailsConstants {

  def createMockBusinessContactDetailsRepo(initialUsers: List[BusinessContactDetails]): IO[MockBusinessContactDetailsRepository] =
    Ref.of[IO, List[BusinessContactDetails]](initialUsers).map(MockBusinessContactDetailsRepository.apply)

  def testContactDetails(
                          id: Option[Int],
                          userId: String,
                          businessId: String
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

  def testCreateBusinessContactDetailsRequest(
                                               userId: String,
                                               businessId: String
                                             ): CreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = userId,
      businessId = businessId,
      businessName = "mikeyCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mikey@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com"
    )


}
