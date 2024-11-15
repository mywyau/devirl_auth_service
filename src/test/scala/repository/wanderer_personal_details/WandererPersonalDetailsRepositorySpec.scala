package repository.wanderer_personal_details

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repository.wanderer_personal_details.mocks.MockWandererPersonalDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererPersonalDetailsRepositorySpec extends SimpleIOSuite {

  def testPersonalDetails(id: Option[Int], user_id: String): WandererPersonalDetails =
    WandererPersonalDetails(
      id = id,
      user_id = user_id,
      first_name = "bob",
      last_name = "smith",
      contact_number = "0123456789",
      email = "user_1@gmail.com",
      company = "apple",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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

  test(".createPersonalDetails() - when given a valid set of contact details should insert the contact details into the postgres db") {

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

}
