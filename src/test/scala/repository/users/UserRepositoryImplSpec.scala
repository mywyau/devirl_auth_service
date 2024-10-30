package repository.users

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.User
import models.users.Wanderer
import repositories.UserRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserRepositoryImplSpec extends SimpleIOSuite {

  // Helper method to create a test user
  def testUser(username: String, contactNumber: String, email: String): User =
    User(
      userId = "user_id_1",
      username = username,
      password_hash = "hashed_password",
      first_name = "John",
      last_name = "Doe",
      contact_number = contactNumber,
      email = email,
      role = Wanderer,
      created_at = LocalDateTime.now()
    )

  // Mock UserRepositoryAlgebra using Cats Ref to simulate state
  case class MockUserRepository(ref: Ref[IO, List[User]]) extends UserRepositoryAlgebra[IO] {
    override def createUser(user: User): IO[Int] =
      ref.modify(users => (user :: users, 1)) // Simulate inserting the user and returning success

    override def findByUsername(username: String): IO[Option[User]] =
      ref.get.map(_.find(_.username == username)) // Simulate finding the user by username

    override def findByContactNumber(contactNumber: String): IO[Option[User]] =
      ref.get.map(_.find(_.contact_number == contactNumber)) // Simulate finding the user by contact number

    override def findByEmail(email: String): IO[Option[User]] =
      ref.get.map(_.find(_.email == email)) // Simulate finding the user by email
  }

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[User]): IO[MockUserRepository] =
    Ref.of[IO, List[User]](initialUsers).map(MockUserRepository.apply)

  // Tests

  test(".createUser() - should successfully create a new user") {
    val newUser = testUser("newuser", "123456789", "newuser@example.com")

    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.createUser(newUser)
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
}
