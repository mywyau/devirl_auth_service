package services.auth

import cats.effect.IO
import models.users.{Admin, UserProfile, UserLoginRequest, Wanderer}
import services.auth.algebra.UserAuth
import services.auth.constants.AuthenticationServiceConstants.*
import services.auth.mocks.AuthenticationServiceMocks.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object AuthenticationServiceSpec extends SimpleIOSuite {

  test("loginUser should return Right(User) for valid credentials") {

    val passwordServiceMock = new MockPasswordService(expectedHash = "hashed_password")
    val userRepositoryMock = new MockUserRepository(users)
    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)

    val request = UserLoginRequest("username", "password")

    for {
      result <- authService.loginUser(request)
    } yield expect(result.contains(testUser))
  }

  test("loginUser should return Left(Invalid password) for incorrect password") {

    val invalidPasswordRequest = UserLoginRequest("username", "wrong_password")
    val passwordServiceMock = new MockPasswordService(expectedHash = "hashed_wrong_password")
    val userRepositoryMock = new MockUserRepository(users)
    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)

    for {
      result <- authService.loginUser(invalidPasswordRequest)
    } yield expect(result == Left("Invalid password"))
  }

  test("loginUser should return Left(Username not found) for unknown user") {

    val unknownUserRequest = UserLoginRequest("unknown_user", "password")
    val passwordServiceMock = new MockPasswordService("hashed_password")
    val userRepositoryMock = new MockUserRepository(users)
    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)

    for {
      result <- authService.loginUser(unknownUserRequest)
    } yield expect(result == Left("Username not found"))
  }

  test(".authUser() - should return Some(User) for a valid token") {

    val passwordServiceMock = new MockPasswordService("hashed_password")
    val userRepositoryMock = new MockUserRepository(users)
    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)

    for {
      result <- authService.authUser("valid_token")
    } yield expect(result ==
      Some(
        testUser.copy(created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0))
      )
    )
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

  test("authorize should return Right(UserAuth) if user has required role") {

    val passwordServiceMock = new MockPasswordService("hashed_password")
    val userRepositoryMock = new MockUserRepository(users)
    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)

    val userAuth = UserAuth[IO](testUser)

    for {
      result <- authService.authorize(userAuth, Admin)
    } yield expect.all(
      result.isRight,
      result.exists(_.user == testUser)
    )
  }

  test("authorize should return Left(Forbidden) if user does not have required role") {

    val passwordServiceMock = new MockPasswordService("hashed_password")
    val userRepositoryMock = new MockUserRepository(users)
    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)

    val userAuth = UserAuth[IO](testUser.copy(role = Wanderer)) // Assume UserRole is another role

    for {
      result <- authService.authorize(userAuth, Admin)
    } yield expect(result.isLeft) and expect(result == Left("Forbidden"))
  }

  //  test("rbacMiddleware should allow access if user has required role") {
  //    val request = Request[IO]().withHeaders(org.http4s.Headers(org.http4s.Header.Raw(ci"Authorization", "Bearer valid_token")))
  //    val middleware = authService.rbacMiddleware(Admin)
  //
  //    for {
  //      maybeAuth <- middleware(request).value
  //    } yield expect(maybeAuth.contains(UserAuth(testUser)))
  //  }
  //
  //  test("rbacMiddleware should deny access if user lacks required role") {
  //    val request = Request[IO]().withHeaders(org.http4s.Headers(org.http4s.Header.Raw(ci"Authorization", "Bearer valid_token")))
  //    val userWithDifferentRole = testUser.copy(role = Wanderer)
  //    val userRepositoryMock = new MockUserRepository(Map("username" -> userWithDifferentRole))
  //    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)
  //    val middleware = authService.rbacMiddleware(Admin)
  //
  //    for {
  //      maybeAuth <- middleware(request).value
  //    } yield expect(maybeAuth.isEmpty)
  //  }


  //  test("rbacMiddleware should allow access if user has required role") {
  //    val request = Request[IO]().withHeaders(org.http4s.Headers(org.http4s.Header.Raw(ci"Authorization", "Bearer valid_token")))
  //    val middleware = authService.rbacMiddleware(Admin)
  //
  //    middleware(Kleisli { contextRequest =>
  //      OptionT.liftF(IO.pure(
  //        Response[IO](Ok).withEntity(
  //          expect(contextRequest.context == UserAuth(testUser))
  //        )
  //      ))
  //    }).apply(request).value.flatMap {
  //      case Some(response) => IO(expect(response.status == Ok))
  //      case None => IO(failure("Expected Some(Response), but got None"))
  //    }
  //  }
  //
  //  test("rbacMiddleware should deny access if user lacks required role") {
  //    val request = Request[IO]().withHeaders(org.http4s.Headers(org.http4s.Header.Raw(ci"Authorization", "Bearer valid_token")))
  //    val userWithDifferentRole = testUser.copy(role = Wanderer)
  //    val userRepositoryMock = new MockUserRepository(Map("username" -> userWithDifferentRole))
  //    val authService = new AuthenticationServiceImpl[IO](userRepositoryMock, passwordServiceMock)
  //    val middleware = authService.rbacMiddleware(Admin)
  //
  //    middleware(Kleisli { _ =>
  //      OptionT.none[IO, Response[IO]] // Deny access by returning None
  //    }).apply(request).value.flatMap {
  //      case Some(_) => IO(failure("Expected None, but got Some(Response)"))
  //      case None => IO(success)
  //    }
  //  }

}
