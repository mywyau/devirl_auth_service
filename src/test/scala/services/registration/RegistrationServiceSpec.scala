package services.registration

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.effect.IO
import models.auth.{EmailAlreadyExists, PasswordNoSpecialCharacters, UniqueUser, UsernameAlreadyExists}
import models.users.*
import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest
import services.auth.constants.RegistrationServiceConstants.*
import services.registration.mocks.RegistrationServiceMocks.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object RegistrationServiceSpec extends SimpleIOSuite {

  test(".uniqueUsernameAndEmail() - should pass when all fields are unique") {

    val userRepository = new MockUserLoginDetailsRepository() // No users in the database
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService) // No password service needed for this test

    for {
      result <- service.uniqueUsernameAndEmail(uniqueRequest)
    } yield {
      expect(result == Valid(UniqueUser))
    }
  }

  test(".uniqueUsernameAndEmail() - should fail when username already exists") {

    val userRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)

    for {
      _ <- service.uniqueUsernameAndEmail(requestWithDuplicateUsername)
      result <- service.uniqueUsernameAndEmail(requestWithDuplicateUsername)
    } yield {
      expect(result == Invalid(List(UsernameAlreadyExists)))
    }
  }

  test(".uniqueUsernameAndEmail() - should fail when email already exists") {
    val userRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)

    val requestWithDuplicateEmail = uniqueRequest.copy(email = existingUser.email)
    for {
      _ <- service.uniqueUsernameAndEmail(requestWithDuplicateEmail)
      result <- service.uniqueUsernameAndEmail(requestWithDuplicateEmail)
    } yield {
      expect(result == Invalid(List(EmailAlreadyExists)))
    }
  }

  test(".uniqueUser() - should fail when multiple fields are not unique") {
    val userRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)

    val requestWithMultipleConflicts =
      uniqueRequest.copy(
        username = existingUser.username,
        email = existingUser.email
      )

    for {
      result <- service.uniqueUsernameAndEmail(requestWithMultipleConflicts)
    } yield {
      expect(result == Invalid(List(UsernameAlreadyExists, EmailAlreadyExists)))
    }
  }

  test(".registerUser() - should succeed with valid input") {

    val userRepository = new MockUserLoginDetailsRepository()
    val passwordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](userRepository, passwordService)


    service.registerUser(validRequest).map {
      case Valid(user) =>
        expect.all(
          user.username == "newuser",
          user.password_hash == "hashedPassword",
          user.email == "newuser@example.com"
        )
      case Invalid(listOfErrors) => failure(s"$listOfErrors")

    }
  }

  test(".registerUser() - should fail when password validation fails") {

    val mockUserRepository = new MockUserLoginDetailsRepository()
    val mockPasswordService = new MockPasswordService(passwordValidationResult = Invalid(List(PasswordNoSpecialCharacters)), hashedPassword = "")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List(PasswordNoSpecialCharacters))
        )
    }
  }

  test(".registerUser() - should fail when username already exists") {

    val validRequest: UserSignUpRequest = {
      UserSignUpRequest(
        user_id = "user_id_1",
        username = "existinguser",
        password = "ValidPass123!",
        email = "newuser@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val existingUser: UserLoginDetails = {
      UserLoginDetails(
        id = Some(2),
        user_id = "user_id_2",
        username = "existinguser",
        password_hash = "hashedpassword",
        email = "existing@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List(UsernameAlreadyExists))
        )
    }
  }

  test(".registerUser() - should fail when email already exists") {

    val validRequest: UserSignUpRequest = {
      UserSignUpRequest(
        user_id = "user_id_1",
        username = "newuser",
        password = "ValidPass123!",
        email = "same_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val existingUser: UserLoginDetails = {
      UserLoginDetails(
        id = Some(2),
        user_id = "user_id_2",
        username = "existinguser",
        password_hash = "hashedpassword",
        email = "same_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List(EmailAlreadyExists))
        )
    }
  }

  test(".registerUser() - should fail and return multiple errors") {

    val validRequest: UserSignUpRequest = {
      UserSignUpRequest(
        user_id = "user_id_1",
        username = "same_user_name",
        password = "ValidPass123!",
        email = "same_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val existingUser: UserLoginDetails = {
      UserLoginDetails(
        id = Some(2),
        user_id = "user_id_2",
        username = "same_user_name",
        password_hash = "hashedpassword",
        email = "same_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("same_user_name" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List(UsernameAlreadyExists, EmailAlreadyExists))
        )
    }
  }

  test(".validateUnique() - should return username already exists error") {

    val userRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)

    for {
      result <- service.validateUsernameUnique(requestWithDuplicateUsername.username)
    } yield {
      expect(result == Invalid(NonEmptyList.of(UsernameAlreadyExists)))
    }
  }

  test(".validateUnique() - should return email already exists error") {

    val userRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")

    val service = new RegistrationServiceImpl[IO](userRepository, mockPasswordService)
    val requestWithDuplicateUsername = uniqueRequest.copy(email = existingUser.email)

    for {
      result <- service.validateEmailUnique(requestWithDuplicateUsername.email)
    } yield {
      expect(result == Invalid(NonEmptyList.of(EmailAlreadyExists)))
    }
  }
}
