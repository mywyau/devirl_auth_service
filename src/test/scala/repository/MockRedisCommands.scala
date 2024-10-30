package repository

import cats.effect.{IO, Ref}
import repositories.RefreshTokenRepositoryAlgebra

import java.time.Instant

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

// Helper function to create a fresh instance of MockRefreshTokenRepository
object MockRefreshTokenRepository {
  def create(): IO[MockRefreshTokenRepository] =
    Ref.of[IO, Map[String, (String, Instant)]](Map.empty).map(new MockRefreshTokenRepository(_))
}

import cats.effect.IO
import weaver.SimpleIOSuite

object RedisRefreshTokenRepositorySpec extends SimpleIOSuite {

  // Helper to create a fresh instance of the mock repository
  def createRepo(): IO[RefreshTokenRepositoryAlgebra[IO]] = MockRefreshTokenRepository.create()

  test("storeToken should save a token with expiration") {
    for {
      repo <- createRepo()
      token = "testToken"
      userId = "testUserId"
      expiration = Instant.now.plusSeconds(900)
      _ <- repo.storeToken(token, userId, expiration)
      storedUserId <- repo.findUserIdByToken(token)
    } yield expect(storedUserId.contains(userId))
  }

  test("findUserIdByToken should return user ID if token exists and is valid") {
    for {
      repo <- createRepo()
      token = "validToken"
      userId = "user123"
      expiration = Instant.now.plusSeconds(900)
      _ <- repo.storeToken(token, userId, expiration)
      userIdOpt <- repo.findUserIdByToken(token)
    } yield expect(userIdOpt.contains(userId))
  }

  test("findUserIdByToken should return None if token does not exist") {
    for {
      repo <- createRepo()
      userIdOpt <- repo.findUserIdByToken("nonExistentToken")
    } yield expect(userIdOpt.isEmpty)
  }

  test("findUserIdByToken should return None if token is expired") {
    for {
      repo <- createRepo()
      token = "expiredToken"
      userId = "user123"
      expiration = Instant.now.minusSeconds(10) // already expired
      _ <- repo.storeToken(token, userId, expiration)
      userIdOpt <- repo.findUserIdByToken(token)
    } yield expect(userIdOpt.isEmpty)
  }

  test("revokeToken should delete token and return deletion count of 1 if token existed") {
    for {
      repo <- createRepo()
      token = "tokenToDelete"
      userId = "user123"
      expiration = Instant.now.plusSeconds(900)
      _ <- repo.storeToken(token, userId, expiration)
      deletionCount <- repo.revokeToken(token)
      tokenExistsAfterDeletion <- repo.findUserIdByToken(token)
    } yield expect(deletionCount == 1L) and expect(tokenExistsAfterDeletion.isEmpty)
  }

  test("revokeToken should return deletion count of 0 if token did not exist") {
    for {
      repo <- createRepo()
      deletionCount <- repo.revokeToken("nonExistentToken")
    } yield expect(deletionCount == 0L)
  }
}
