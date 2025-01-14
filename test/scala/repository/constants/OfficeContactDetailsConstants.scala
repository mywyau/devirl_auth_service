package repository.constants

import cats.data.Validated.Valid
import cats.effect.kernel.Ref
import cats.effect.IO
import java.time.LocalDateTime
import mocks.MockOfficeContactDetailsRepository
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial

object OfficeContactDetailsConstants {

  def testCreateOfficeContactDetailsRequest(businessId: String, office_id: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mikey@gmail.com",
      contactNumber = "07402205071"
    )

  def testContactDetails(businessId: String, office_id: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mikey@gmail.com"),
      contactNumber = Some("07402205071")
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[OfficeContactDetailsPartial]): IO[MockOfficeContactDetailsRepository] =
    Ref.of[IO, List[OfficeContactDetailsPartial]](initialUsers).map(MockOfficeContactDetailsRepository.apply)

}
