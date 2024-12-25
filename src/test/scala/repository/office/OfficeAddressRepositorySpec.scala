package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import repository.office.mocks.MockOfficeAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeAddressRepositorySpec extends SimpleIOSuite {

  def testOfficeAddressRequest(businessId: String, office_id: String): CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = businessId,
      officeId = office_id,
      buildingName = Some("building name"),
      floorNumber = Some("floor 3"),
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("United Kingdom"),
      county = Some("fake County"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(-100),
      longitude = Some(-96.7)
    )

  def testAddress(id: Option[Int], businessId: String, office_id: String): OfficeAddress =
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = office_id,
      buildingName = Some("building name"),
      floorNumber = Some("floor 3"),
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("United Kingdom"),
      county = Some("fake County"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(-100), 
      longitude = Some(-96.7),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[OfficeAddress]): IO[MockOfficeAddressRepository] =
    Ref.of[IO, List[OfficeAddress]](initialUsers).map(MockOfficeAddressRepository.apply)

  test(".findByOfficeId() - should return an address if office_id exists") {
    val existingAddressForUser = testAddress(Some(1), "business_id_1", "office_1")

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      result <- mockRepo.findByOfficeId("office_1")
    } yield expect(result.contains(existingAddressForUser))
  }

  test(".findByOfficeId() - should return None if business_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil)
      result <- mockRepo.findByOfficeId("office_1")
    } yield expect(result.isEmpty)
  }

  test(".createOfficeAddress() - when given a valid office address should insert an address into the postgres db") {

    val testAddressRequest: CreateOfficeAddressRequest = testOfficeAddressRequest("business_id_2", "office_2")
    val testAddressForUser2: OfficeAddress = testAddress(Some(1), "business_id_2", "office_2")
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.create(testAddressRequest)
      findInsertedAddress <- mockRepo.findByOfficeId("office_2")
    } yield expect.all(
      result == Valid(1),
      findInsertedAddress == Some(testAddressForUser2)
    )
  }
}
