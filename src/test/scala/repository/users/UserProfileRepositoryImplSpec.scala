package repository.users

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.adts.{Business, Wanderer}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}
import repository.users.mocks.MockUserProfileRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserProfileRepositoryImplSpec extends SimpleIOSuite {

  def testUser(username: String, contactNumber: String, email: String): UserProfile =
    UserProfile(
      userId = "user_id_1",
      userLoginDetails =
        UserLoginDetails(
          id = Some(1),
          user_id = "user_id_1",
          username = username,
          password_hash = "hashed_password",
          email = email,
          role = Wanderer,
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ),
      first_name = "John",
      last_name = "Doe",
      userAddress =
        UserAddress(
          userId = "user_id_1",
          street = "fake street 1",
          city = "fake city 1",
          country = "UK",
          county = Some("County 1"),
          postcode = "CF3 3NJ",
          created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ),
      contact_number = contactNumber,
      email = email,
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[UserProfile]): IO[MockUserProfileRepository] =
    Ref.of[IO, List[UserProfile]](initialUsers).map(MockUserProfileRepository.apply)


  test(".createUser() - should successfully create a new user") {
    val newUser = testUser("newuser", "123456789", "newuser@example.com")

    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.createUserProfile(newUser)
      users <- mockRepo.ref.get
    } yield expect(result == 1) and expect(users.contains(newUser))
  }

  test(".findByUsername() - should return a user if username exists") {
    val existingUser = testUser("existinguser", "123456789", "existinguser@example.com")

    for {
      mockRepo <- createMockRepo(List(existingUser)) // User already exists
      result <- mockRepo.findByUsername("existinguser")
    } yield expect(result.contains(existingUser))
  }

  test(".findByUsername() - should return None if username does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByUsername("nonexistentuser")
    } yield expect(result.isEmpty)
  }

  test(".findByContactNumber() - should return a user if contact number exists") {
    val existingUser = testUser("user1", "123456789", "user1@example.com")

    for {
      mockRepo <- createMockRepo(List(existingUser)) // User already exists
      result <- mockRepo.findByContactNumber("123456789")
    } yield expect(result.contains(existingUser))
  }

  test(".findByContactNumber() - should return None if contact number does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByContactNumber("nonexistentcontact")
    } yield expect(result.isEmpty)
  }

  test(".findByEmail() - should return a user if email exists") {
    val existingUser = testUser("user1", "123456789", "user1@example.com")
    for {
      mockRepo <- createMockRepo(List(existingUser)) // User already exists
      result <- mockRepo.findByEmail("user1@example.com")
    } yield expect(result.contains(existingUser))
  }

  test(".findByEmail() - should return None if email does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByEmail("nonexistentemail@example.com")
    } yield expect(result.isEmpty)
  }

  test(".findByUserId() - should return a user if userId exists") {
    val existingUser = testUser("user1", "123456789", "user1@example.com")
    for {
      mockRepo <- createMockRepo(List(existingUser)) // User already exists
      result <- mockRepo.findByUserId("user_id_1")
    } yield expect(result.contains(existingUser))
  }

  test(".findByUserId() - should return None if userId does not exist") {
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.findByUserId("nonexistentUserId")
    } yield expect(result.isEmpty)
  }

  test(".updateUserRole() - should update the role if userId exists") {
    val existingUser = testUser("user1", "123456789", "user1@example.com")
    val updatedRole = Business
    for {
      mockRepo <- createMockRepo(List(existingUser))
      updatedUser <- mockRepo.updateUserRole("user_id_1", updatedRole)
    } yield expect(updatedUser.exists(_.role == updatedRole))
  }

  test(".updateUserRole() - should return None if userId does not exist") {
    for {
      mockRepo <- createMockRepo(Nil)
      result <- mockRepo.updateUserRole("nonexistentUserId", Business)
    } yield expect(result.isEmpty)
  }
}
