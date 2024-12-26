package repository.business

import cats.data.Validated.Valid
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import repository.business.mocks.MockBusinessContactDetailsRepository
import repository.constants.BusinessContactDetailsConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessContactDetailsRepositorySpec extends SimpleIOSuite {

  test(".findByBusinessId() - should return the contact details if business_id exists") {

    val userId = "user_id_1"
    val businessId = "business_id_1"

    val existingContactDetailsForUser: BusinessContactDetails = testContactDetails(Some(1), userId, businessId)

    for {
      mockBusinessContactDetailsRepo <- createMockBusinessContactDetailsRepo(List(existingContactDetailsForUser))
      result <- mockBusinessContactDetailsRepo.findByBusinessId(businessId)
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByBusinessId() - should return None if business_id does not exist") {

    for {
      mockBusinessContactDetailsRepo <- createMockBusinessContactDetailsRepo(Nil)
      result <- mockBusinessContactDetailsRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".create() - when given a valid BusinessContactDetails should insert BusinessContactDetails data into the postgres db table") {

    val userId = "user_id_1"
    val businessId = "business_id_1"

    val testContactDetailsForUser: BusinessContactDetails = testContactDetails(Some(1), userId, businessId)
    val testCreateRequest: CreateBusinessContactDetailsRequest = testCreateBusinessContactDetailsRequest(userId, businessId)

    for {
      mockBusinessContactDetailsRepo <- createMockBusinessContactDetailsRepo(List())
      result <- mockBusinessContactDetailsRepo.create(testCreateRequest)
      findInsertedContactDetails <- mockBusinessContactDetailsRepo.findByBusinessId(businessId)
    } yield expect.all(
      result == Valid(1),
      findInsertedContactDetails == Some(testContactDetailsForUser)
    )
  }
}
