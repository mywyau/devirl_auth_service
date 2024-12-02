package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.business_address.service.BusinessAddress
import repository.business.mocks.MockBusinessAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressRepositorySpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], userId: String): BusinessAddress =
    BusinessAddress(
      id = id,
      userId = userId,
      businessId = Some("business1"),
      buildingName = Some("building 1"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[BusinessAddress]): IO[MockBusinessAddressRepository] =
    Ref.of[IO, List[BusinessAddress]](initialUsers).map(MockBusinessAddressRepository.apply)

  test(".findByUserId() - should return an address if user_id exists") {
    val existingAddressForUser = testAddress(Some(1), "user_id_1")

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.contains(existingAddressForUser))
  }

  test(".findByUserId() - should return None if user_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createUserAddress() - when given a valid address should insert an address into the postgres db") {
    val testAddressForUser2: BusinessAddress = testAddress(Some(2), "user_id_2")

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createBusinessAddress(testAddressForUser2)
      findInsertedAddress <- mockRepo.findByUserId("user_id_2")
    } yield expect.all(
      result == Valid(1),
      findInsertedAddress == Some(testAddressForUser2)
    )
  }
}
