package services.auth

import cats.effect.{IO, Ref}
import models.users.{UserAddress, UserLoginDetails, UserProfile, Wanderer}
import services.auth.mocks.{MockRefreshTokenRepository, MockTokenService, MockUserRepository}
import weaver.SimpleIOSuite

import java.time.{Instant, LocalDateTime}

object SessionManagerServiceSpec extends SimpleIOSuite {

  val testUser =
    UserProfile(
      userId = "userId",
      UserLoginDetails(
        userId = "userId",
        username = "username",
        password_hash = "hashed_password"
      ),
      first_name = "John",
      last_name = "Doe",
      UserAddress(
        userId = "user_id_1",
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        created_at = LocalDateTime.now()
      ),
      contact_number = "07402205071",
      email = "john@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test(".generateTokens() - should return a new access and refresh token") {
    for {
      // Create fresh state for mock repository
      ref <- Ref.of[IO, Map[String, (String, Instant)]](Map.empty)
      mockRefreshTokenRepo = new MockRefreshTokenRepository(ref)

      // Initialize other mocks
      mockTokenService = new MockTokenService
      mockUserRepository = new MockUserRepository

      // Initialize the SessionManager with mocks
      sessionManager = new SessionManager[IO](mockTokenService, mockRefreshTokenRepo, mockUserRepository)

      // Test logic
      tokens <- sessionManager.generateTokens(testUser)
      storedUserId <- mockRefreshTokenRepo.findUserIdByToken(tokens._2)
    } yield {
      expect.all(
        tokens._1.startsWith("accessToken-"),
        storedUserId == Some(testUser.userId)
      )
    }
  }

  test(".refreshAccessToken() - should return a new access token if refresh token is valid") {
    for {
      // Create fresh state for mock repository
      ref <- Ref.of[IO, Map[String, (String, Instant)]](Map.empty)
      mockRefreshTokenRepo = new MockRefreshTokenRepository(ref)

      // Initialize other mocks
      mockTokenService = new MockTokenService
      mockUserRepository = new MockUserRepository

      // Initialize the SessionManager with mocks
      sessionManager = new SessionManager[IO](mockTokenService, mockRefreshTokenRepo, mockUserRepository)

      // Test logic
      tokens <- sessionManager.generateTokens(testUser)
      newAccessTokenOpt <- sessionManager.refreshAccessToken("username", tokens._2)
    } yield expect(newAccessTokenOpt.exists(_.startsWith("accessToken-")))
  }

  test(".refreshAccessToken() - should return None for invalid refresh token") {
    for {
      // Create fresh state for mock repository
      ref <- Ref.of[IO, Map[String, (String, Instant)]](Map.empty)
      mockRefreshTokenRepo = new MockRefreshTokenRepository(ref)

      // Initialize other mocks
      mockTokenService = new MockTokenService
      mockUserRepository = new MockUserRepository

      // Initialize the SessionManager with mocks
      sessionManager = new SessionManager[IO](mockTokenService, mockRefreshTokenRepo, mockUserRepository)

      // Test logic
      newAccessTokenOpt <- sessionManager.refreshAccessToken(username = "username", refreshToken = "invalidToken")
    } yield expect(newAccessTokenOpt.isEmpty)
  }

  test(".refreshAccessToken() - should return None if username does not exist") {
    for {
      // Create fresh state for mock repository
      ref <- Ref.of[IO, Map[String, (String, Instant)]](Map.empty)
      mockRefreshTokenRepo = new MockRefreshTokenRepository(ref)

      // Initialize other mocks
      mockTokenService = new MockTokenService
      mockUserRepository = new MockUserRepository

      // Initialize the SessionManager with mocks
      sessionManager = new SessionManager[IO](mockTokenService, mockRefreshTokenRepo, mockUserRepository)

      // Test logic
      newAccessTokenOpt <- sessionManager.refreshAccessToken("unknown_username", "someRefreshToken")
    } yield expect(newAccessTokenOpt.isEmpty)
  }

  test(".revokeSession() - should remove the refresh token") {
    for {
      // Create fresh state for mock repository
      ref <- Ref.of[IO, Map[String, (String, Instant)]](Map.empty)
      mockRefreshTokenRepo = new MockRefreshTokenRepository(ref)

      // Initialize other mocks
      mockTokenService = new MockTokenService
      mockUserRepository = new MockUserRepository

      // Initialize the SessionManager with mocks
      sessionManager = new SessionManager[IO](mockTokenService, mockRefreshTokenRepo, mockUserRepository)

      // Test logic
      tokens <- sessionManager.generateTokens(testUser)
      _ <- sessionManager.revokeSession(tokens._2)
      userIdAfterRevoke <- mockRefreshTokenRepo.findUserIdByToken(tokens._2)
    } yield expect(userIdAfterRevoke.isEmpty)
  }
}
