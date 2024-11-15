package repository.wanderer_contact_details

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.wanderer_personal_details.service.WandererContactDetails
import repository.wanderer_contact_details.mocks.MockWandererContactDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererContactDetailsRepositorySpec extends SimpleIOSuite {

  def testContactDetails(id: Option[Int], user_id: String): WandererContactDetails =
    WandererContactDetails(
      id = id,
      user_id = user_id,
      contact_number = "0123456789",
      email = "user_1@gmail.com",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def createMockRepo(initialUsers: List[WandererContactDetails]): IO[MockWandererContactDetailsRepository] =
    Ref.of[IO, List[WandererContactDetails]](initialUsers).map(MockWandererContactDetailsRepository.apply)

  test(".findByUserId() - should return a user's contact details if user_id exists") {
    val existingContactDetailsForUser = testContactDetails(Some(1), "user_id_1")

    for {
      mockRepo <- createMockRepo(List(existingContactDetailsForUser))
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByUserId() - should return None if user_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createContactDetails() - when given a valid set of contact details should insert the contact details into the postgres db") {

    val testContactDetailsForUser2: WandererContactDetails = testContactDetails(Some(2), "user_id_2")

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createContactDetails(testContactDetailsForUser2)
      findInsertedAddress <- mockRepo.findByUserId("user_id_2")
    } yield
      expect.all(
        result == 1,
        findInsertedAddress == Some(testContactDetailsForUser2)
      )
  }

}
