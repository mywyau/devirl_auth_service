package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import mocks.MockBusinessContactDetailsRepository

import java.time.LocalDateTime
import models.business.contact_details.BusinessContactDetailsPartial

object BusinessContactDetailsConstants {

  def createMockBusinessContactDetailsRepo(initialUsers: List[BusinessContactDetailsPartial]): IO[MockBusinessContactDetailsRepository] =
    Ref.of[IO, List[BusinessContactDetailsPartial]](initialUsers).map(MockBusinessContactDetailsRepository.apply)

  def testContactDetails(userId: String,businessId: String): BusinessContactDetailsPartial =
    BusinessContactDetailsPartial(
      userId = userId,
      businessId = businessId,
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mikey@gmail.com"),
      contactNumber = Some("07402205071"),
      websiteUrl = Some("mikey.com")
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
