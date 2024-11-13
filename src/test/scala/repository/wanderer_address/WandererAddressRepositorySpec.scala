package repository.wanderer_address

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.wanderer_address.service.WandererAddress
import repository.wanderer_address.mocks.MockWandererAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererAddressRepositorySpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], user_id: String): WandererAddress =
    WandererAddress(
      id = Some(1),
      user_id = user_id,
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 3NJ",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[WandererAddress]): IO[MockWandererAddressRepository] =
    Ref.of[IO, List[WandererAddress]](initialUsers).map(MockWandererAddressRepository.apply)

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

    val testAddressForUser2: WandererAddress = testAddress(Some(2), "user_id_2")

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createUserAddress(testAddressForUser2)
      findInsertedAddress <- mockRepo.findByUserId("user_id_2")
    } yield
      expect.all(
        result == 1,
        findInsertedAddress == Some(testAddressForUser2)
      )
  }

}
