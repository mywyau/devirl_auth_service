package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import repository.business.mocks.MockBusinessContactDetailsRepository
import repository.constants.BusinessContactDetailsConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessContactDetailsRepositorySpec extends SimpleIOSuite {

  test(".findByBusinessId() - should return the contact details if business_id exists") {
    val existingContactDetailsForUser = testContactDetails(Some(1), "user_id_1","business_id_1", "business_1")

    for {
      mockRepo <- createMockRepo(List(existingContactDetailsForUser))
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByBusinessId() - should return None if business_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil)
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createContactDetails() - when given a valid BusinessContactDetails should insert BusinessContactDetails data into the postgres db table") {

    val testContactDetailsForUser2: BusinessContactDetails = testContactDetails(Some(2),"user_id_1", "business_id_2", "business_2")
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createContactDetails(testContactDetailsForUser2)
      findInsertedContactDetails <- mockRepo.findByBusinessId("business_id_2")
    } yield expect.all(
      result == Valid(1),
      findInsertedContactDetails == Some(testContactDetailsForUser2)
    )
  }
}
