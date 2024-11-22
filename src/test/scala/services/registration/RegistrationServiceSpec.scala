package services.registration

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.adts.Wanderer
import models.users.registration.*
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest
import services.auth.constants.RegistrationServiceConstants.*
import services.authentication.registration.RegistrationServiceImpl
import services.registration.mocks.RegistrationServiceMocks.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object RegistrationServiceSpec extends SimpleIOSuite {
  
  test(".uniqueUsernameAndEmail() - should pass when all fields are unique") {

    val mockUserRepository = new MockUserLoginDetailsRepository() 
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository,mockWandererAddressRepo, mockWandererPersonalDetailsRepository,  mockPasswordService)

    for {
      result <- service.uniqueUsernameAndEmail(uniqueRequest)
    } yield {
      expect(result == Valid(UniqueUser))
    }
  }

  test(".uniqueUsernameAndEmail() - should fail when username already exists") {

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)
    
    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)

    for {
      _ <- service.uniqueUsernameAndEmail(requestWithDuplicateUsername)
      result <- service.uniqueUsernameAndEmail(requestWithDuplicateUsername)
    } yield {
      expect(result == Invalid(List(UsernameAlreadyExists)))
    }
  }

  test(".uniqueUsernameAndEmail() - should fail when email already exists") {
    
    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)


    val requestWithDuplicateEmail = uniqueRequest.copy(email = existingUser.email)
    for {
      _ <- service.uniqueUsernameAndEmail(requestWithDuplicateEmail)
      result <- service.uniqueUsernameAndEmail(requestWithDuplicateEmail)
    } yield {
      expect(result == Invalid(List(EmailAlreadyExists)))
    }
  }

  test(".uniqueUser() - should fail when multiple fields are not unique") {
    
    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)
    
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

    val mockUserRepository = new MockUserLoginDetailsRepository()
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)


    service.registerUser(validRequest).map {
      case Valid(user) =>
        expect.all(
          user.username == "newuser",
          user.passwordHash == "hashedPassword",
          user.email == "newuser@example.com"
        )
      case Invalid(listOfErrors) => failure(s"$listOfErrors")

    }
  }

  test(".registerUser() - should fail when password validation fails") {

    val mockUserRepository = new MockUserLoginDetailsRepository()
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(passwordValidationResult = Invalid(List(PasswordNoSpecialCharacters)), hashedPassword = "")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)
    
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
        userId = "user_id_1",
        username = "existinguser",
        password = "ValidPass123!",
        email = "newuser@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val existingUser: UserLoginDetails = {
      UserLoginDetails(
        id = Some(2),
        userId = "user_id_2",
        username = "existinguser",
        passwordHash = "hashedpassword",
        email = "existing@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }
    
    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)

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
        userId = "user_id_1",
        username = "newuser",
        password = "ValidPass123!",
        email = "same_email@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val existingUser: UserLoginDetails = {
      UserLoginDetails(
        id = Some(2),
        userId = "user_id_2",
        username = "existinguser",
        passwordHash = "hashedpassword",
        email = "same_email@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)

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
        userId = "user_id_1",
        username = "same_user_name",
        password = "ValidPass123!",
        email = "same_email@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val existingUser: UserLoginDetails = {
      UserLoginDetails(
        id = Some(2),
        userId = "user_id_2",
        username = "same_user_name",
        passwordHash = "hashedpassword",
        email = "same_email@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("same_user_name" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)

    service.registerUser(validRequest).map {
      result =>
        expect(
          result == Invalid(List(UsernameAlreadyExists, EmailAlreadyExists))
        )
    }
  }

  test(".validateUnique() - should return username already exists error") {

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(validRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)
    
    val requestWithDuplicateUsername = uniqueRequest.copy(username = existingUser.username)

    for {
      result <- service.validateUsernameUnique(requestWithDuplicateUsername.username)
    } yield {
      expect(result == Invalid(NonEmptyList.of(UsernameAlreadyExists)))
    }
  }

  test(".validateUnique() - should return email already exists error") {

    val mockUserRepository = new MockUserLoginDetailsRepository(Map("existinguser" -> existingUser))
    val mockWandererAddressRepo = new MockWandererAddressRepository()
    val mockWandererPersonalDetailsRepository = new MockWandererPersonalDetailsRepository()
    val mockPasswordService = new MockPasswordService(Valid(uniqueRequest.password), "hashedPassword")
    val service = new RegistrationServiceImpl[IO](mockUserRepository, mockWandererAddressRepo, mockWandererPersonalDetailsRepository, mockPasswordService)
    
    val requestWithDuplicateUsername = uniqueRequest.copy(email = existingUser.email)

    for {
      result <- service.validateEmailUnique(requestWithDuplicateUsername.email)
    } yield {
      expect(result == Invalid(NonEmptyList.of(EmailAlreadyExists)))
    }
  }
}
