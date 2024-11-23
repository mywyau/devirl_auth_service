package repository.wanderer_personal_details

import cats.effect.IO
import cats.effect.kernel.Ref
import models.wanderer.wanderer_personal_details.service.WandererPersonalDetails
import repository.wanderer_personal_details.mocks.MockWandererPersonalDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererPersonalDetailsRepositorySpec extends SimpleIOSuite {

  def testPersonalDetails(id: Option[Int], userId: String): WandererPersonalDetails =
    WandererPersonalDetails(
      id = id,
      userId = userId,
      firstName = Some("bob"),
      lastName = Some("smith"),
      contactNumber = Some("0123456789"),
      email = Some("user_1@gmail.com"),
      company = Some("apple"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def createMockRepo(initialUsers: List[WandererPersonalDetails]): IO[MockWandererPersonalDetailsRepository] =
    Ref.of[IO, List[WandererPersonalDetails]](initialUsers).map(MockWandererPersonalDetailsRepository.apply)

  test(".findByUserId() - should return a user's contact details if user_id exists") {
    val existingPersonalDetailsForUser = testPersonalDetails(Some(1), "user_id_1")

    for {
      mockRepo <- createMockRepo(List(existingPersonalDetailsForUser))
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.contains(existingPersonalDetailsForUser))
  }

  test(".findByUserId() - should return None if user_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createPersonalDetails() - when given a valid set of contact details should insert the contact details into the mock repository") {
    val testPersonalDetailsForUser2: WandererPersonalDetails = testPersonalDetails(Some(2), "user_id_2")

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createPersonalDetails(testPersonalDetailsForUser2)
      findInsertedAddress <- mockRepo.findByUserId("user_id_2")
    } yield
      expect.all(
        result == 1,
        findInsertedAddress == Some(testPersonalDetailsForUser2)
      )
  }

  test(".updatePersonalDetailsDynamic - should update all fields for an existing user") {

    val existingDetails = testPersonalDetails(Some(3), "user_id_3")

    for {
      mockRepo <- createMockRepo(List(existingDetails))
      updated <- mockRepo.updatePersonalDetailsDynamic(
        userId = "user_id_3",
        contactNumber = Some("9876543210"),
        firstName = Some("John"),
        lastName = Some("Doe"),
        email = Some("updated.john@example.com"),
        company = Some("Updated Corp")
      )
      fetchedDetails <- mockRepo.findByUserId("user_id_3")
    } yield expect(updated.nonEmpty) and expect(
      fetchedDetails ==
        Some(
          existingDetails.copy(
            contactNumber = Some("9876543210"),
            firstName = Some("John"),
            lastName = Some("Doe"),
            email = Some("updated.john@example.com"),
            company = Some("Updated Corp")
          )
        )
    )
  }

  test(".updatePersonalDetailsDynamic - should partially update fields for an existing user") {
    val existingDetails = testPersonalDetails(Some(4), "user_id_4")

    for {
      mockRepo <- createMockRepo(List(existingDetails))
      updated <- mockRepo.updatePersonalDetailsDynamic(
        userId = "user_id_4",
        contactNumber = None,
        firstName = Some("Updated Bob"),
        lastName = None,
        email = Some("partial.bob@example.com"),
        company = None
      )
      fetchedDetails <- mockRepo.findByUserId("user_id_4")
    } yield expect(updated.nonEmpty) and expect(
      fetchedDetails.contains(
        existingDetails.copy(
          firstName = Some("Updated Bob"),
          contactNumber = None,
          lastName = None,
          email = Some("partial.bob@example.com"),
          company = None
        )
      )
    )
  }

  test(".updatePersonalDetailsDynamic - should do nothing if no fields are provided") {
    val existingDetails = testPersonalDetails(Some(5), "user_id_5")

    for {
      mockRepo <- createMockRepo(List(existingDetails))
      updated <- mockRepo.updatePersonalDetailsDynamic(
        userId = "user_id_5",
        contactNumber = None,
        firstName = None,
        lastName = None,
        email = None,
        company = None
      )
      fetchedDetails <- mockRepo.findByUserId("user_id_5")
    } yield expect.all(
      updated.contains(
        existingDetails.copy(
          firstName = None,
          lastName = None,
          contactNumber = None,
          email = None,
          company = None
        )
      ),
      fetchedDetails.contains(
        existingDetails.copy(
          firstName = None,
          lastName = None,
          contactNumber = None,
          email = None,
          company = None
        )
      )
    )
  }

  test(".updatePersonalDetailsDynamic - should return None if user_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil)
      result <- mockRepo.updatePersonalDetailsDynamic(
        userId = "nonexistent_user_id",
        contactNumber = Some("9876543210"),
        firstName = Some("New John"),
        lastName = Some("New Doe"),
        email = Some("new.john@example.com"),
        company = Some("New Corp")
      )
    } yield expect(result.isEmpty)
  }
}
