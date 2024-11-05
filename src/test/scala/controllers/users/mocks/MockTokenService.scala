package controllers.users.mocks

import cats.data.Validated
import cats.effect.IO
import models.users.*
import models.users.database.UserLoginDetails
import models.users.requests.UserSignUpRequest
import org.http4s.*
import services.auth.algebra.*
import services.auth.{InvalidToken, TokenStatus}

import java.time.Instant

class MockTokenService extends TokenServiceAlgebra[IO] {

  override def createToken(user: UserProfile, expiration: Instant): IO[String] =
    IO.pure(s"accessToken-${user.userId}")

  override def validateToken(token: String): IO[Either[TokenStatus, String]] =
    if (token.contains("valid")) IO.pure(Right("userId")) else IO.pure(Left(InvalidToken))

  override def invalidateToken(token: String): IO[Boolean] = ???

  override def refreshAccessToken(refreshToken: String): IO[Option[String]] = ???
}

// Mock AuthenticationService
case class MockAuthService(
                            loginUserMock: UserLoginRequest => IO[Either[String, UserProfile]]
                          ) extends AuthenticationServiceAlgebra[IO] {

  override def loginUser(request: UserLoginRequest): IO[Either[String, UserProfile]] =
    loginUserMock(request)

  override def authUser(token: String): IO[Option[UserProfile]] = IO(None)

  override def authorize(userAuth: UserAuth[IO], requiredRole: Role): IO[Either[String, UserAuth[IO]]] =
    ???

  override def updateUserRole(requestingUserId: String, targetUserId: String, newRole: Role): IO[Either[String, UserProfile]] =
    ???
}


class MockRegistrationService(
                               registerUserMock: UserSignUpRequest => IO[Validated[List[String], UserLoginDetails]]
                             ) extends RegistrationServiceAlgebra[IO] {


  override def registerUser(request: UserSignUpRequest): IO[Validated[List[String], UserLoginDetails]] =
    registerUserMock(request)
}