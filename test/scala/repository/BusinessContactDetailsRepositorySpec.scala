package repository

import cats.data.Validated.Valid
import mocks.MockBusinessContactDetailsRepository
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.database.CreateSuccess
import repository.constants.BusinessContactDetailsRepoConstants.*
import services.RepositorySpecBase
import testData.TestConstants.*
import weaver.SimpleIOSuite

object BusinessContactDetailsRepositorySpec extends SimpleIOSuite with RepositorySpecBase {

  test(".findByBusinessId() - should return the contact details if business_id exists") {

    val existingContactDetailsForUser = testContactDetails(userId1, businessId1)

    for {
      mockBusinessContactDetailsRepo <- createMockBusinessContactDetailsRepo(List(existingContactDetailsForUser))
      result <- mockBusinessContactDetailsRepo.findByBusinessId(businessId1)
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByBusinessId() - should return None if business_id does not exist") {

    for {
      mockBusinessContactDetailsRepo <- createMockBusinessContactDetailsRepo(Nil)
      result <- mockBusinessContactDetailsRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".create() - when given a valid BusinessContactDetails should insert BusinessContactDetails data into the postgres db table") {

    val testCreateRequest: CreateBusinessContactDetailsRequest = testCreateBusinessContactDetailsRequest(userId1, businessId1)
    val expectedContactDetails = testContactDetails(userId1, businessId1)

    for {
      mockBusinessContactDetailsRepo <- createMockBusinessContactDetailsRepo(List())
      result <- mockBusinessContactDetailsRepo.create(testCreateRequest)
      findInsertedContactDetails <- mockBusinessContactDetailsRepo.findByBusinessId(businessId1)
    } yield expect.all(
      result == Valid(CreateSuccess),
      findInsertedContactDetails == Some(expectedContactDetails)
    )
  }
}
