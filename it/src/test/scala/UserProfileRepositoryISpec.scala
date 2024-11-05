import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import models.users.{UserAddress, UserLoginDetails, UserProfile, Wanderer}
import repositories.users.UserProfileRepositoryImpl
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

// Define a wrapper case class to help with runtime type issues
case class TransactorResource(xa: Transactor[IO])

// Define test suite using ResourceSuite to share the Transactor[IO] within this file
class UserProfileRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = TransactorResource // needed for the shared resource, set the type of 'Res' here to be used in tests

  // Return the wrapped Transactor resource
  def sharedResource: Resource[IO, TransactorResource] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      // Create the table if it doesn't exist
      _ <- Resource.eval(
        sql"""
        CREATE TABLE IF NOT EXISTS user_profile (
          id BIGSERIAL PRIMARY KEY,
          userId VARCHAR(255) NOT NULL,
          username VARCHAR(255) NOT NULL,
          password_hash TEXT NOT NULL,
          first_name VARCHAR(255) NOT NULL,
          last_name VARCHAR(255) NOT NULL,
          street VARCHAR(255) NOT NULL,
          city VARCHAR(255) NOT NULL,
          country VARCHAR(255) NOT NULL,
          county VARCHAR(255),
          postcode VARCHAR(255) NOT NULL,
          contact_number VARCHAR(100) NOT NULL,
          email VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
      """.update.run.transact(transactor.xa).void
      )
      // Delete all existing data from the user_profile table before tests start
      _ <- Resource.eval(
        sql"DELETE FROM user_profile".update.run.transact(transactor.xa).void
      )
    } yield transactor
    setup
  }


  def sampleUser(userId: String, username: String, contactNumber: String, email: String) =
    UserProfile(
      userId = userId,
      UserLoginDetails(
        userId = userId,
        username = username,
        password_hash = "hashedpassword"
      ),
      first_name = "Test",
      last_name = "User",
      UserAddress(
        userId = userId,
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      contact_number = contactNumber,
      email = email,
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


  // Test case to verify user creation and retrieval by username
  test(".createUser() - should insert a new user") { transactorResource =>

    val userRepository = new UserProfileRepositoryImpl[IO](transactorResource.xa)

    val user = sampleUser(userId = "user_id_1", username = "mikey5922", contactNumber = "07402205071", email = "mikey5922@gmail.com")

    for {
      result <- userRepository.createUserProfile(user)
      userOpt <- userRepository.findByUsername("mikey5922")
    } yield expect(result == 1) and expect(userOpt.contains(user))
  }

  test(".findByUsername() - should return the user if username exists") { transactorResource =>

    val user = sampleUser("user_id_2", "mikey5923", "07402205072", "mikey5923@gmail,com")

    val userRepository = new UserProfileRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUserProfile(user)
      userOpt <- userRepository.findByUsername("mikey5923")
    } yield expect(userOpt.contains(user))
  }

  test(".findByContactNumber() - should return the user if contact number exists") { transactorResource =>

    val user = sampleUser("user_id_3", "mikey5924", "1234567890", "mikey5924@gmail,com")

    val userRepository = new UserProfileRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUserProfile(user)
      userOpt <- userRepository.findByContactNumber("1234567890")
    } yield expect(userOpt.contains(user))
  }

  test(".findByEmail() - should return the user if email exists") { transactorResource =>

    val user = sampleUser("user_id_4", "mikey5925", "07402205074", "mikey5925@gmail,com")

    val userRepository = new UserProfileRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUserProfile(user)
      userOpt <- userRepository.findByEmail("mikey5925@gmail,com")
    } yield expect(userOpt.contains(user))
  }

  test(".findByUsername() - should return None if username does not exist") { transactorResource =>
    val userRepository = new UserProfileRepositoryImpl[IO](transactorResource.xa)
    for {
      userOpt <- userRepository.findByUsername("nonexistentuser")
    } yield expect(userOpt.isEmpty)
  }
}
