package repository.constants

import cats.data.Validated.Valid
import cats.effect.kernel.Ref
import cats.effect.IO
import java.time.LocalDateTime
import mocks.MockOfficeContactDetailsRepository
import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import testData.TestConstants.*

object OfficeContactDetailsConstants {

  def testCreateOfficeContactDetailsRequest(businessId: String, office_id: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1
    )

  def testContactDetails(businessId: String, office_id: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = Some(primaryContactFirstName1),
      primaryContactLastName = Some(primaryContactLastName1),
      contactEmail = Some(contactEmail1),
      contactNumber = Some(contactNumber1)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[OfficeContactDetailsPartial]): IO[MockOfficeContactDetailsRepository] =
    Ref.of[IO, List[OfficeContactDetailsPartial]](initialUsers).map(MockOfficeContactDetailsRepository.apply)

}
