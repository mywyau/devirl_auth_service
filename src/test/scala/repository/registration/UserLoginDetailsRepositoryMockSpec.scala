package repository.registration

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.UserLoginDetails
import repository.registration.mocks.MockUserLoginDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object MockUserLoginDetailsRepository {
  def apply(initialData: Map[String, UserLoginDetails] = Map.empty): IO[MockUserLoginDetailsRepository] =
    Ref.of[IO, Map[String, UserLoginDetails]](initialData).map(new MockUserLoginDetailsRepository(_))
}


object UserLoginDetailsRepositoryMockSpec extends SimpleIOSuite {

  def testUserLoginDetails(userId: String, username: String, email: String): UserLoginDetails =
    UserLoginDetails(
      id = None,
      userId = userId,
      username = username,
      passwordHash = "hashed_password",
      email = email,
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test(".createUserLoginDetails - should successfully insert a user") {
    val user = testUserLoginDetails("user_id_1", "username_1", "user1@example.com")

    for {
      mockRepo <- MockUserLoginDetailsRepository()
      rowsInserted <- mockRepo.createUserLoginDetails(user)
      fetchedUser <- mockRepo.findByUserId("user_id_1")
    } yield expect(rowsInserted == 1) and expect(fetchedUser.contains(user))
  }

  test(".findByUserId - should return a user if userId exists") {
    val user = testUserLoginDetails("user_id_2", "username_2", "user2@example.com")

    for {
      mockRepo <- MockUserLoginDetailsRepository(Map("user_id_2" -> user))
      fetchedUser <- mockRepo.findByUserId("user_id_2")
    } yield expect(fetchedUser.contains(user))
  }

  test(".findByUserId - should return None if userId does not exist") {
    for {
      mockRepo <- MockUserLoginDetailsRepository()
      fetchedUser <- mockRepo.findByUserId("nonexistent_user_id")
    } yield expect(fetchedUser.isEmpty)
  }

  test(".updateUserLoginDetails - should update all fields for an existing user") {
    val originalUser = testUserLoginDetails("user_id_3", "username_3", "user3@example.com")
    val updatedUser = originalUser.copy(
      username = "updated_username",
      email = "updated_email@example.com"
    )

    for {
      mockRepo <- MockUserLoginDetailsRepository(Map("user_id_3" -> originalUser))
      updated <- mockRepo.updateUserLoginDetails("user_id_3", updatedUser)
      fetchedUser <- mockRepo.findByUserId("user_id_3")
    } yield expect(updated.contains(updatedUser)) and expect(fetchedUser.contains(updatedUser))
  }

  test(".updateUserLoginDetailsDynamic - should partially update fields for an existing user") {
    val originalUser = testUserLoginDetails("user_id_4", "username_4", "user4@example.com")

    for {
      mockRepo <- MockUserLoginDetailsRepository(Map("user_id_4" -> originalUser))
      updated <- mockRepo.updateUserLoginDetailsDynamic(
        userId = "user_id_4",
        username = Some("partial_username"),
        passwordHash = None,
        email = None,
        role = None
      )
      fetchedUser <- mockRepo.findByUserId("user_id_4")
    } yield expect(updated.nonEmpty) and expect(
      fetchedUser.exists { user =>
        user.username == "partial_username" &&
          user.passwordHash == "hashed_password" &&
          user.email == "user4@example.com"
      }
    )
  }

  test(".updateUserLoginDetailsDynamic - should return None if userId does not exist") {
    for {
      mockRepo <- MockUserLoginDetailsRepository()
      result <- mockRepo.updateUserLoginDetailsDynamic(
        userId = "nonexistent_user_id",
        username = Some("new_username"),
        passwordHash = None,
        email = None,
        role = None
      )
    } yield expect(result.isEmpty)
  }
}
