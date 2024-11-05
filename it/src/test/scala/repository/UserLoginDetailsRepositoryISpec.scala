package repository

import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import models.users.Wanderer
import models.users.database.UserLoginDetails
import repositories.users.UserLoginDetailsRepositoryImpl
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

// Define test suite using ResourceSuite to share the Transactor[IO] within this file
class UserLoginDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = TransactorResource // needed for the shared resource, set the type of 'Res' here to be used in tests

  // Return the wrapped Transactor resource
  def sharedResource: Resource[IO, TransactorResource] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      // Create the table if it doesn't exist
      _ <- Resource.eval(
        sql"""
        CREATE TABLE IF NOT EXISTS user_login_details (
          id BIGSERIAL PRIMARY KEY,
          user_id VARCHAR(255) NOT NULL,
          username VARCHAR(255) NOT NULL,
          password_hash TEXT NOT NULL,
          email VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
      """.update.run.transact(transactor.xa).void
      )
      // Delete all existing data from the user_profile table before tests start
      //      _ <- Resource.eval(
      //        sql"DELETE FROM user_login_details".update.run.transact(transactor.xa).void
      //      )
      _ <- Resource.eval(
        sql"TRUNCATE TABLE user_login_details RESTART IDENTITY".update.run.transact(transactor.xa).void
      )
    } yield transactor
    setup
  }


  def sampleUser(id: Option[Int],
                 userId: String,
                 username: String,
                 contactNumber: String,
                 email: String
                ) =
    UserLoginDetails(
      id = id,
      user_id = userId,
      username = username,
      password_hash = "hashedpassword",
      email = email,
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Test case to verify user creation and retrieval by username
  test(".createUser() - should insert a new user") { transactorResource =>

    val userRepository = new UserLoginDetailsRepositoryImpl[IO](transactorResource.xa)

    val user = sampleUser(Some(1), userId = "user_id_1", username = "mikey5922", contactNumber = "07402205071", email = "mikey5922@gmail.com")

    for {
      result <- userRepository.createUserLoginDetails(user)
      userOpt <- userRepository.findByUsername("mikey5922")
    } yield expect(result == 1) and expect(userOpt.contains(user))
  }

  test(".findByUsername() - should return the user if username exists") { transactorResource =>

    val user = sampleUser(Some(3), "user_id_2", "mikey5923", "07402205072", "mikey5923@gmail.com")

    val userRepository = new UserLoginDetailsRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUserLoginDetails(user)
      userOpt <- userRepository.findByUsername("mikey5923")
    } yield expect(userOpt.contains(user))
  }

  test(".findByEmail() - should return the user if email exists") { transactorResource =>

    val user = sampleUser(Some(2), "user_id_4", "mikey5925", "07402205074", "mikey5925@gmail.com")

    val userRepository = new UserLoginDetailsRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUserLoginDetails(user)
      userOpt <- userRepository.findByEmail("mikey5925@gmail.com")
    } yield expect(userOpt.contains(user))
  }

  test(".findByUsername() - should return None if username does not exist") { transactorResource =>
    val userRepository = new UserLoginDetailsRepositoryImpl[IO](transactorResource.xa)
    for {
      userOpt <- userRepository.findByUsername("nonexistentuser")
    } yield expect(userOpt.isEmpty)
  }
}
