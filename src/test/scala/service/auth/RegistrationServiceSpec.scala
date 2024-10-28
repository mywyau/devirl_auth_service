package service.auth

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import cats.implicits.*
import models.users.*
import repositories.UserRepositoryAlgebra
import services.{PasswordServiceAlgebra, RegistrationServiceImpl, UniqueUser}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object RegistrationServiceSpec extends SimpleIOSuite {

  // Mock Implementations
  class MockUserRepository(
                            existingUsers: Map[String, User] = Map.empty
                          ) extends UserRepositoryAlgebra[IO] {

    def showAllUsers = IO.pure(existingUsers)

    override def findByUsername(username: String): IO[Option[User]] = IO.pure(existingUsers.get(username))

    override def findByContactNumber(contactNumber: String): IO[Option[User]] = IO.pure(existingUsers.values.find(_.contact_number == contactNumber))

    override def findByEmail(email: String): IO[Option[User]] = IO.pure(existingUsers.values.find(_.email == email))

    override def createUser(user: User): IO[Int] = IO.pure(1) // Assume user creation always succeeds
  }

  class MockPasswordService(
                             passwordValidationResult: Validated[List[String], String],
                             hashedPassword: String
                           ) extends PasswordServiceAlgebra[IO] {
    override def validatePassword(plainTextPassword: String): Validated[List[String], String] = passwordValidationResult

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(hashedPassword)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] = IO.pure(true)
  }

  // Sample data
  val validRequest =
    UserRegistrationRequest(
      username = "newuser",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      contact_number = "1234567890",
      email = "newuser@example.com",
      role = Wanderer
    )

  val existingUser =
    User(
      username = "existinguser",
      password_hash = "hashedpassword",
      first_name = "First",
      last_name = "Last",
      contact_number = "1234567890",
      email = "existing@example.com",
      role = Wanderer,
      created_at = LocalDateTime.now()
    )


  val uniqueRequest =
    UserRegistrationRequest(
      username = "newuser",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      contact_number = "0987654321",
      email = "newuser@example.com",
      role = Wanderer
    )

  test(".uniqueUser() - should pass when all fields are unique") {

    val userRepository = new MockUserRepository() // No users in the database
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService) // No password service needed for this test

    service.uniqueUser(uniqueRequest).map {
      case Valid(validation) => expect(validation == UniqueUser)
      case Invalid(errors) => failure(s"Expected Unique but got errors: $errors")
    }
  }

  test(".uniqueUser() - should fail when username already exists") {
    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)

    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)


    for {
      users <- userRepository.showAllUsers
      _ <- IO.println(users)
      _ <- service.uniqueUser(requestWithDuplicateUsername)
      result <- service.uniqueUser(requestWithDuplicateUsername).map {
        case Valid(_) =>
          failure("Expected Invalid but got Valid")
        case Invalid(errors) =>
          expect(errors.contains("username already exists"))
      }
    } yield {
      result
    }

    service.uniqueUser(requestWithDuplicateUsername).map {
      case Valid(_) => failure("Expected Invalid but got Valid")
      case Invalid(errors) =>
        expect(errors.contains("username already exists"))
    }
  }
  //
  //  test(".uniqueUser() - should fail when contact number already exists") {
  //    val userRepository = new MockUserRepository(Map("someuser" -> existingUser))
  //    val service = new RegistrationServiceImpl[IO](userRepository, null)
  //
  //    val requestWithDuplicateContact = uniqueRequest.copy(contact_number = existingUser.contact_number)
  //    service.uniqueUser(requestWithDuplicateContact).map {
  //      case Valid(_) => failure("Expected Invalid but got Valid")
  //      case Invalid(errors) =>
  //        expect(errors.contains("contact number already exists"))
  //    }
  //  }
  //
  //  test(".uniqueUser() - should fail when email already exists") {
  //    val userRepository = new MockUserRepository(Map("someuser" -> existingUser))
  //    val service = new RegistrationServiceImpl[IO](userRepository, null)
  //
  //    val requestWithDuplicateEmail = uniqueRequest.copy(email = existingUser.email)
  //    service.uniqueUser(requestWithDuplicateEmail).map {
  //      case Valid(_) => failure("Expected Invalid but got Valid")
  //      case Invalid(errors) =>
  //        expect(errors.contains("email already exists"))
  //    }
  //  }
  //
  //  test(".uniqueUser() - should fail when multiple fields are not unique") {
  //    val userRepository = new MockUserRepository(Map("someuser" -> existingUser))
  //    val service = new RegistrationServiceImpl[IO](userRepository, null)
  //
  //    val requestWithMultipleConflicts = uniqueRequest.copy(
  //      username = existingUser.username,
  //      contact_number = existingUser.contact_number,
  //      email = existingUser.email
  //    )
  //    service.uniqueUser(requestWithMultipleConflicts).map {
  //      case Valid(_) => failure("Expected Invalid but got Valid")
  //      case Invalid(errors) =>
  //        expect(errors.contains("username already exists")) and
  //          expect(errors.contains("contact number already exists")) and
  //          expect(errors.contains("email already exists"))
  //    }
  //  }

  //  test(".registerUser() - should succeed with valid input") {
  //
  //    val userRepository = new MockUserRepository()
  //    val passwordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
  //    val service = new RegistrationServiceImpl[IO](userRepository, passwordService)
  //
  //    service.registerUser(validRequest).map {
  //      case Valid(user) =>
  //        expect(user.username == validRequest.username) and
  //          expect(user.password_hash == "hashedPassword") and
  //          expect(user.email == validRequest.email)
  //      case Invalid(errors) => failure(s"Expected Valid but got errors: $errors")
  //    }
  //  }
  //
  //  test(".registerUser() - should fail when password validation fails") {
  //
  //    val userRepository = new MockUserRepository()
  //    val passwordService = new MockPasswordService(Invalid(List("Password must contain a special character.")), "")
  //    val service = new RegistrationServiceImpl[IO](userRepository, passwordService)
  //
  //    service.registerUser(validRequest).map {
  //      case Valid(_) =>
  //        failure("Expected Invalid but got Valid")
  //      case Invalid(errors) =>
  //        expect(errors.contains("Password must contain a special character."))
  //    }
  //  }

  test(".registerUser() - should fail when username already exists") {

    val mockUserRepository = new MockUserRepository(Map("newuser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      case Valid(_) =>
        failure("Expected Invalid but got Valid")
      case Invalid(errors) =>
        expect(errors.contains("username already exists"))
    }
  }

  //  test(".registerUser() - should fail when contact number already exists") {
  //
  //    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
  //    val passwordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
  //    val service = new RegistrationServiceImpl[IO](userRepository, passwordService)
  //
  //    val conflictingRequest = validRequest.copy(contact_number = existingUser.contact_number)
  //    service.registerUser(conflictingRequest).map {
  //      case Valid(_) =>
  //        failure("Expected Invalid but got Valid")
  //      case Invalid(errors) =>
  //        expect(errors.contains("contact number already exists"))
  //    }
  //  }
  //
  //  test(".registerUser() - should fail when email already exists") {
  //
  //    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
  //    val passwordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
  //    val service = new RegistrationServiceImpl[IO](userRepository, passwordService)
  //
  //    val conflictingRequest = validRequest.copy(email = existingUser.email)
  //    service.registerUser(conflictingRequest).map {
  //      case Valid(_) =>
  //        failure("Expected Invalid but got Valid")
  //      case Invalid(errors) =>
  //        expect(errors.contains("email already exists"))
  //    }
  //  }
}
