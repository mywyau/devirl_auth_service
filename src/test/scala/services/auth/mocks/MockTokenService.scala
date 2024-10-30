package services.auth.mocks

import cats.effect.IO
import models.users.{User, Wanderer}
import repositories.{RefreshTokenRepositoryAlgebra, UserRepositoryAlgebra}
import services.auth.TokenServiceAlgebra
import cats.effect.{IO, Ref}
import cats.syntax.all._
import java.time.Instant
import scala.concurrent.duration._

import java.time.{Instant, LocalDateTime}

class MockTokenService extends TokenServiceAlgebra[IO] {

  override def createToken(user: User, expiration: Instant): IO[String] =
    IO.pure(s"accessToken-${user.userId}")

  override def validateToken(token: String): IO[Option[String]] =
    if (token.contains("valid")) IO.pure(Some("userId")) else IO.pure(None)
}


class MockRefreshTokenRepository(ref: Ref[IO, Map[String, (String, Instant)]]) extends RefreshTokenRepositoryAlgebra[IO] {

  override def storeToken(token: String, userId: String, expiration: Instant): IO[Unit] =
    ref.update(tokens => tokens + (token -> (userId, expiration)))

  override def findUserIdByToken(token: String): IO[Option[String]] =
    ref.get.map(_.get(token).filter { case (_, expiration) => expiration.isAfter(Instant.now) }.map(_._1))

  override def revokeToken(token: String): IO[Long] =
    ref.modify { tokens =>
      if (tokens.contains(token)) (tokens - token, 1L)
      else (tokens, 0L)
    }
}

// Companion object for easy creation
object MockRefreshTokenRepository {
  def create(): IO[MockRefreshTokenRepository] =
    Ref.of[IO, Map[String, (String, Instant)]](Map.empty).map(new MockRefreshTokenRepository(_))
}

class MockUserRepository extends UserRepositoryAlgebra[IO] {

  private val users =
    Map("username" ->
      User(
        userId = "userId",
        username = "username",
        password_hash = "hashed_password",
        first_name = "John",
        last_name = "Doe",
        contact_number = "07402205071",
        email = "john@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    )

  override def findByUsername(username: String): IO[Option[User]] = IO.pure(users.get(username))

  override def createUser(user: User): IO[Int] = ???

  override def findByContactNumber(contactNumber: String): IO[Option[User]] = ???

  override def findByEmail(email: String): IO[Option[User]] = ???
}
