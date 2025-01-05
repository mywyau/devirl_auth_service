package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.address.BusinessAddress
import models.business.address.requests.CreateBusinessAddressRequest
import repository.business.mocks.MockBusinessAddressRepository
import repository.constants.BusinessAddressRepositoryConstants.*
import weaver.SimpleIOSuite
import models.database.CreateSuccess
import models.database.DeleteSuccess

object BusinessAddressRepositorySpec extends SimpleIOSuite {

  test(".findByBusinessId() - should return an address if business_id_1 exists") {

    val existingAddressForUser = testAddress("user_id_1", "business_id_1")

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.contains(existingAddressForUser))
  }

  test(".findByBusinessId() - should return None if business_id_1 does not exist") {
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createBusinessAddress() - when given a valid business address should insert an address into the postgres db") {

    val testBusinessAddressRequest: CreateBusinessAddressRequest = testCreateBusinessAddressRequest("user_id_2", "business_id_2")
    val testAddressForUser2 = testAddress("user_id_2", "business_id_2")

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.create(testBusinessAddressRequest)
      findInsertedAddress <- mockRepo.findByBusinessId("business_id_2")
    } yield expect.all(
      result == Valid(CreateSuccess),
      findInsertedAddress == Some(testAddressForUser2)
    )
  }

  test(".delete() - when given a valid businessId should delete the business address details") {

    val existingAddressForUser = testAddress("user_id_3", "business_id_3")

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      findInitiallyCreatedAddress <- mockRepo.findByBusinessId("business_id_3")
      result <- mockRepo.delete("business_id_3")
      findInsertedAddress <- mockRepo.findByBusinessId("business_id_3")
    } yield expect.all(
      findInitiallyCreatedAddress == Some(existingAddressForUser),
      result == Valid(DeleteSuccess),
      findInsertedAddress == None
    )
  }
}
