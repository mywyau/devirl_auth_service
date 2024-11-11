package services.auth

import cats.effect.IO
import models.users.adts.{Admin, Wanderer}
import models.users.login.requests.UserLoginRequest
import models.users.wanderer_profile.profile.UserProfile
import services.auth.algebra.UserAuth
import services.auth.constants.AuthenticationServiceConstants.*
import services.auth.mocks.AuthenticationServiceMocks.*
import weaver.SimpleIOSuite

object AuthenticationServiceSpec extends SimpleIOSuite {

//  test(".loginUser() - should return Right(User) for valid credentials") {
//
//    val mockPasswordService = new MockPasswordService(expectedHash = "hashed_password")
//    val mockUserLoginDetailsRepository = new MockUserLoginDetailsRepository(Map("username" -> testUserLoginDetails))
//    val mockUserProfileRepository = new MockUserProfileRepository(Map("username" -> testUserProfile))
//    val authService = new AuthenticationServiceImpl[IO](mockUserLoginDetailsRepository, mockUserProfileRepository, mockPasswordService)
//
//    val request = UserLoginRequest("username", "password")
//
//    for {
//      result <- authService.loginUser(request)
//    } yield expect(result == Right(testUserLoginDetails))
//  }
//
//  test(".loginUser() - should return Left(Invalid password) for incorrect password") {
//
//    val invalidPasswordRequest = UserLoginRequest("username", "wrong_password")
//
//    val mockPasswordService = new MockPasswordService(expectedHash = "hashed_wrong_password")
//    val mockUserLoginDetailsRepository = new MockUserLoginDetailsRepository(Map("username" -> testUserLoginDetails))
//    val mockUserProfileRepository = new MockUserProfileRepository(Map("username" -> testUserProfile))
//    val authService = new AuthenticationServiceImpl[IO](mockUserLoginDetailsRepository, mockUserProfileRepository, mockPasswordService)
//
//    for {
//      result <- authService.loginUser(invalidPasswordRequest)
//    } yield expect(result == Left("Invalid password"))
//  }
//
//  test(".loginUser() - should return Left(Username not found) for unknown user") {
//
//    val unknownUserRequest = UserLoginRequest("unknown_user", "password")
//
//    val mockPasswordService = new MockPasswordService(expectedHash = "hashed_password")
//    val mockUserLoginDetailsRepository = new MockUserLoginDetailsRepository(Map("username" -> testUserLoginDetails))
//    val mockUserProfileRepository = new MockUserProfileRepository(Map("username" -> testUserProfile))
//    val authService = new AuthenticationServiceImpl[IO](mockUserLoginDetailsRepository, mockUserProfileRepository, mockPasswordService)
//
//    for {
//      result <- authService.loginUser(unknownUserRequest)
//    } yield expect(result == Left("Username not found"))
//  }

  test(".authUser() - should return Some(User) for a valid token") {

    val mockPasswordService = new MockPasswordService(expectedHash = "hashed_password")
    val mockUserLoginDetailsRepository = new MockUserLoginDetailsRepository(Map("username" -> testUserLoginDetails))
    val mockUserProfileRepository = new MockUserProfileRepository(Map("username" -> testUserProfile))
    val authService = new AuthenticationServiceImpl[IO](mockUserLoginDetailsRepository, mockUserProfileRepository, mockPasswordService)

    for {
      result <- authService.authUser("valid_token")
    } yield expect(result == Some(testUserProfile))
  }

  //  test("authUser should return None for an invalid token") {
  //
  //    val passwordServiceMock = new MockPasswordService("hashed_password")
  //    val userRepositoryMock = new MockUserRepository(users)
  //    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)
  //
  //    for {
  //      result <- authService.authUser("invalid_token")
  //    } yield expect(result.isEmpty) //TODO: Fix - Broken test since we have no implementation
  //  }

  test(".authorize() - should return Right(UserAuth) if user has required role") {

    val mockPasswordService = new MockPasswordService(expectedHash = "hashed_password")
    val mockUserLoginDetailsRepository = new MockUserLoginDetailsRepository(Map("username" -> testUserLoginDetails))
    val mockUserProfileRepository = new MockUserProfileRepository(Map("username" -> testUserProfile))
    val authService = new AuthenticationServiceImpl[IO](mockUserLoginDetailsRepository, mockUserProfileRepository, mockPasswordService)


    val userAuth = UserAuth[IO](testUserProfile)

    for {
      result <- authService.authorize(userAuth, Admin)
    } yield expect.all(
      result.isRight,
      result.exists(_.user == testUserProfile)
    )
  }

  test(".authorize() - should return Left(Forbidden) if user does not have required role") {

    val mockPasswordService = new MockPasswordService(expectedHash = "hashed_password")
    val mockUserLoginDetailsRepository = new MockUserLoginDetailsRepository(Map("username" -> testUserLoginDetails))
    val mockUserProfileRepository = new MockUserProfileRepository(Map("username" -> testUserProfile))
    val authService = new AuthenticationServiceImpl[IO](mockUserLoginDetailsRepository, mockUserProfileRepository, mockPasswordService)

    val userAuth = UserAuth[IO](testUserProfile.copy(role = Wanderer)) // Assume UserRole is another role

    for {
      result <- authService.authorize(userAuth, Admin)
    } yield expect(result.isLeft) and expect(result == Left("Forbidden"))
  }

}
