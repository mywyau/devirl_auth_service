package services.auth

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.effect.IO
import cats.implicits.*
import models.users.*
import repositories.UserRepositoryAlgebra
import services.auth.constants.RegistrationConstants._
import services.{PasswordServiceAlgebra, RegistrationServiceImpl, UniqueUser}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object RegistrationServiceSpec extends SimpleIOSuite {
  
  class MockUserRepository(
                            existingUsers: Map[String, User] = Map.empty
                          ) extends UserRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, User]] = IO.pure(existingUsers)

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

  test(".uniqueUser() - should pass when all fields are unique") {

    val userRepository = new MockUserRepository() // No users in the database
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService) // No password service needed for this test

    for {
      result <- service.uniqueUser(uniqueRequest)
    } yield {
      expect(result == Valid(UniqueUser))
    }
  }

  test(".uniqueUser() - should fail when username already exists") {

    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)

    for {
      _ <- service.uniqueUser(requestWithDuplicateUsername)
      result <- service.uniqueUser(requestWithDuplicateUsername)
    } yield {
      expect(result == Invalid(List("username already exists")))
    }
  }

  test(".uniqueUser() - should fail when contact number already exists") {
    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)

    val requestWithDuplicateContact = uniqueRequest.copy(contact_number = existingUser.contact_number)

    for {
      _ <- service.uniqueUser(requestWithDuplicateContact)
      result <- service.uniqueUser(requestWithDuplicateContact)
    } yield {
      expect(result == Invalid(List("contact_number already exists")))
    }
  }

  test(".uniqueUser() - should fail when email already exists") {
    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)

    val requestWithDuplicateEmail = uniqueRequest.copy(email = existingUser.email)
    for {
      _ <- service.uniqueUser(requestWithDuplicateEmail)
      result <- service.uniqueUser(requestWithDuplicateEmail)
    } yield {
      expect(result == Invalid(List("email already exists")))
    }
  }

  test(".uniqueUser() - should fail when multiple fields are not unique") {
    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)

    val requestWithMultipleConflicts =
      uniqueRequest.copy(
        username = existingUser.username,
        contact_number = existingUser.contact_number,
        email = existingUser.email
      )

    for {
      result <- service.uniqueUser(requestWithMultipleConflicts)
    } yield {
      expect(result == Invalid(List("username already exists", "contact_number already exists", "email already exists")))
    }
  }

  test(".registerUser() - should succeed with valid input") {

    val userRepository = new MockUserRepository()
    val passwordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, passwordService)


    service.registerUser(validRequest).map {
      case Valid(user) =>
        expect.all(
          user.username == "newuser",
          user.password_hash == "hashedPassword",
          user.email == "newuser@example.com",
          user.contact_number == "1234567890",
        )
      case Invalid(listOfErrors) => failure(s"$listOfErrors")

    }
  }

  test(".registerUser() - should fail when password validation fails") {

    val mockUserRepository = new MockUserRepository()
    val mockPasswordService = new MockPasswordService(Invalid(List("Password must contain a special character.")), "")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List("Password must contain a special character."))
        )
    }
  }

  test(".registerUser() - should fail when username already exists") {

    // Sample data
    val validRequest: UserRegistrationRequest = {
      UserRegistrationRequest(
        username = "existinguser",
        password = "ValidPass123!",
        first_name = "First",
        last_name = "Last",
        contact_number = "1234567891",
        email = "newuser@example.com",
        role = Wanderer
      )
    }

    val existingUser: User = {
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
    }

    val mockUserRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List("username already exists"))
        )
    }
  }

  test(".registerUser() - should fail when contact_number already exists") {

    // Sample data
    val validRequest: UserRegistrationRequest = {
      UserRegistrationRequest(
        username = "newuser",
        password = "ValidPass123!",
        first_name = "First",
        last_name = "Last",
        contact_number = "1234567890",
        email = "newuser@example.com",
        role = Wanderer
      )
    }

    val existingUser: User = {
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
    }

    val mockUserRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List("contact_number already exists"))
        )
    }
  }

  test(".registerUser() - should fail when email already exists") {

    // Sample data
    val validRequest: UserRegistrationRequest = {
      UserRegistrationRequest(
        username = "newuser",
        password = "ValidPass123!",
        first_name = "First",
        last_name = "Last",
        contact_number = "1234567891",
        email = "same_email@example.com",
        role = Wanderer
      )
    }

    val existingUser: User = {
      User(
        username = "existinguser",
        password_hash = "hashedpassword",
        first_name = "First",
        last_name = "Last",
        contact_number = "1234567890",
        email = "same_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.now()
      )
    }

    val mockUserRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List("email already exists"))
        )
    }
  }

  test(".registerUser() - should fail and return multiple errors") {

    // Sample data
    val validRequest: UserRegistrationRequest = {
      UserRegistrationRequest(
        username = "same_user_name",
        password = "ValidPass123!",
        first_name = "First",
        last_name = "Last",
        contact_number = "1234567890",
        email = "same_email@example.com",
        role = Wanderer
      )
    }

    val existingUser: User = {
      User(
        username = "same_user_name",
        password_hash = "hashedpassword",
        first_name = "First",
        last_name = "Last",
        contact_number = "1234567890",
        email = "same_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.now()
      )
    }

    val mockUserRepository = new MockUserRepository(Map("same_user_name" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List("username already exists", "contact_number already exists", "email already exists"))
        )
    }
  }

  test(".validateUnique() - should return username already exists error") {

    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)

    for {
      result <- service.validateUnique("username", requestWithDuplicateUsername.username, userRepository.findByUsername)
    } yield {
      expect(result == Invalid(NonEmptyList.of("username already exists")))
    }
  }

  test(".validateUnique() - should return email already exists error") {

    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(email = existingUser.email)

    for {
      result <- service.validateUnique("email", requestWithDuplicateUsername.email, userRepository.findByEmail)
    } yield {
      expect(result == Invalid(NonEmptyList.of("email already exists")))
    }
  }

  test(".validateUnique() - should return contact_number already exists error") {

    val userRepository = new MockUserRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(contact_number = existingUser.contact_number)

    for {
      result <- service.validateUnique("contact_number", requestWithDuplicateUsername.contact_number, userRepository.findByContactNumber)
    } yield {
      expect(result == Invalid(NonEmptyList.of("contact_number already exists")))
    }
  }

}
