package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import repository.business.mocks.MockBusinessAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressRepositorySpec extends SimpleIOSuite {

  def testAddressRequest(userId: String, businessId: Option[String]): BusinessAddressRequest =
    BusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("mikeyCorp"),
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

  def testAddress(id: Option[Int], userId: String): BusinessAddress =
    BusinessAddress(
      id = id,
      userId = userId,
      businessId = Some("business1"),
      businessName = Some("mikeyCorp"),
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

  def createMockRepo(initialUsers: List[BusinessAddress]): IO[MockBusinessAddressRepository] =
    Ref.of[IO, List[BusinessAddress]](initialUsers).map(MockBusinessAddressRepository.apply)

  test(".findByBusinessId() - should return an address if user_id exists") {

    val existingAddressForUser = testAddress(Some(1), "user_id_1")

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      result <- mockRepo.findByBusinessId("user_id_1")
    } yield expect(result.contains(existingAddressForUser))
  }

  test(".findByBusinessId() - should return None if user_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil)
      result <- mockRepo.findByBusinessId("user_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createBusinessAddress() - when given a valid business address should insert an address into the postgres db") {

    val testBusinessAddressRequest: BusinessAddressRequest = testAddressRequest("user_id_2", Some("business1"))
    val testAddressForUser2: BusinessAddress = testAddress(Some(1), "user_id_2")

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createBusinessAddress(testBusinessAddressRequest)
      findInsertedAddress <- mockRepo.findByBusinessId("user_id_2")
    } yield expect.all(
      result == Valid(1),
      findInsertedAddress == Some(testAddressForUser2)
    )
  }
}
