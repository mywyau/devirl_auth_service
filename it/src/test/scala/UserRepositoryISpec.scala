import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import models.users.{User, Wanderer}
import repositories.UserRepositoryImpl
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

// Define a wrapper case class
case class TransactorResource(xa: Transactor[IO])

// Define test suite using ResourceSuite to share the Transactor[IO] within this file
class UserRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = TransactorResource // needed for the shared resource, set the type of 'Res' here to be used in tests

  // Return the wrapped Transactor resource
  def sharedResource: Resource[IO, TransactorResource] =
    global.getOrFailR[TransactorResource]()

  def sampleUser(username: String, contactNumber: String, email: String) =
    User(
      username = username,
      password_hash = "hashedpassword",
      first_name = "Test",
      last_name = "User",
      contact_number = contactNumber,
      email = email,
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


  // Test case to verify user creation and retrieval by username
  test("createUser should insert a new user") { transactorResource =>
    val userRepository = new UserRepositoryImpl[IO](transactorResource.xa)

    val user = sampleUser("mikey5922", "07402205071", "mikey5922@gmail,com")

    for {
      result <- userRepository.createUser(user)
      userOpt <- userRepository.findByUsername("mikey5922")
    } yield expect(result == 1) and expect(userOpt.contains(user))
  }

  test("findByUsername should return the user if username exists") { transactorResource =>

    val user = sampleUser("mikey5923", "07402205072", "mikey5923@gmail,com")

    val userRepository = new UserRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUser(user)
      userOpt <- userRepository.findByUsername("mikey5923")
    } yield expect(userOpt.contains(user))
  }

  test("findByContactNumber should return the user if contact number exists") { transactorResource =>

    val user = sampleUser("mikey5924", "1234567890", "mikey5924@gmail,com")

    val userRepository = new UserRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUser(user)
      userOpt <- userRepository.findByContactNumber("1234567890")
    } yield expect(userOpt.contains(user))
  }

  test("findByEmail should return the user if email exists") { transactorResource =>

    val user = sampleUser("mikey5925", "07402205074", "mikey5925@gmail,com")

    val userRepository = new UserRepositoryImpl[IO](transactorResource.xa)
    for {
      _ <- userRepository.createUser(user)
      userOpt <- userRepository.findByEmail("mikey5925@gmail,com")
    } yield expect(userOpt.contains(user))
  }

  test("findByUsername should return None if username does not exist") { transactorResource =>
    val userRepository = new UserRepositoryImpl[IO](transactorResource.xa)
    for {
      userOpt <- userRepository.findByUsername("nonexistentuser")
    } yield expect(userOpt.isEmpty)
  }
}
