package repository.wanderer_address

import cats.effect.IO
import cats.effect.kernel.Ref
import models.wanderer.wanderer_address.service.WandererAddress
import repository.wanderer_address.mocks.MockWandererAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererAddressRepositorySpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], userId: String): WandererAddress =
    WandererAddress(
      id = id,
      userId = userId,
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
    } yield expect.all(
      result == 1,
      findInsertedAddress == Some(testAddressForUser2)
    )
  }

  test(".updateAddressDynamic() - should update all fields for an existing address") {
    val originalAddress = testAddress(Some(3), "user_id_3")

    for {
      mockRepo <- createMockRepo(List(originalAddress))
      updated <- mockRepo.updateAddressDynamic(
        userId = "user_id_3",
        street = Some("new street"),
        city = Some("new city"),
        country = Some("new country"),
        county = Some("new county"),
        postcode = Some("NEW123")
      )
      updatedAddress <- mockRepo.findByUserId("user_id_3")
    } yield expect(updated.nonEmpty) and expect(
      updatedAddress.contains(
        originalAddress.copy(
          street = Some("new street"),
          city = Some("new city"),
          country = Some("new country"),
          county = Some("new county"),
          postcode = Some("NEW123")
        )
      )
    )
  }

  test(".updateAddressDynamic() - should partially update fields for an existing address") {

    val originalAddress = testAddress(Some(4), "user_id_4")

    for {
      mockRepo <- createMockRepo(List(originalAddress))
      updated <- mockRepo.updateAddressDynamic(
        userId = "user_id_4",
        street = None,
        city = Some("updated city"),
        country = None,
        county = None,
        postcode = Some("UPD456")
      )
      updatedAddress <- mockRepo.findByUserId("user_id_4")
    } yield expect(updated.nonEmpty) and expect(
      updatedAddress.contains(
        originalAddress.copy(
          street = None,
          city = Some("updated city"),
          country = None,
          county = None,
          postcode = Some("UPD456")
        )
      )
    )
  }

  test(".updateAddressDynamic() - should return None if user_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil)
      result <- mockRepo.updateAddressDynamic(
        userId = "nonexistent_user_id",
        street = Some("some street"),
        city = Some("some city"),
        country = Some("some country"),
        county = Some("some county"),
        postcode = Some("SOME123")
      )
    } yield expect(result.isEmpty)
  }

  test(".updateAddressDynamic() - should do nothing if no fields are provided") {
    val originalAddress = testAddress(Some(5), "user_id_5")

    for {
      mockRepo <- createMockRepo(List(originalAddress))
      updated <- mockRepo.updateAddressDynamic(
        userId = "user_id_5",
        street = None,
        city = None,
        country = None,
        county = None,
        postcode = None
      )
      updatedAddress <- mockRepo.findByUserId("user_id_5")
    } yield
      expect.all(
        updated == Some(
          originalAddress.copy(
            street = None,
            city = None,
            country = None,
            county = None,
            postcode = None
          )
        ),
        updatedAddress == Some(
          originalAddress.copy(
            street = None,
            city = None,
            country = None,
            county = None,
            postcode = None
          )
        )
      )
  }
}
