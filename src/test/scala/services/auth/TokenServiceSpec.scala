package services.auth

import cats.effect.{IO, Ref}
import io.circe.syntax.*
import models.users.database.UserLoginDetails
import models.users.{UserAddress, UserProfile, Wanderer}
import pdi.jwt.{JwtAlgorithm, JwtCirce}
import weaver.SimpleIOSuite

import java.time.{Clock, Instant, LocalDateTime, ZoneOffset}
import scala.concurrent.duration.*

// In-memory Redis mock using a Cats Effect Ref
class InMemoryRedisMock(state: Ref[IO, Map[String, String]]) extends TokenRedisCommands[IO] {
  override def get(key: String): IO[Option[String]] =
    state.get.map(_.get(key))

  override def setEx(key: String, value: String, ttl: FiniteDuration): IO[Boolean] =
    state.update(_ + (key -> value)).as(true)
}


// Test Suite
object TokenServiceSpec extends SimpleIOSuite {

  val fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)

  val testUser =
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        id = Some(1),
        user_id = "user_id_1",
        username = "username",
        password_hash = "hashed_password",
        email = "john@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      contact_number = "07402205071",
      email = "john@example.com",
      role = models.users.Admin,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val secretKey = "supersecretkey"

  def createTokenService(secretKey: String, clock: Clock): IO[TokenServiceImpl[IO]] =
    Ref.of[IO, Map[String, String]](Map.empty).map { ref =>
      val redisMock = new InMemoryRedisMock(ref)
      new TokenServiceImpl[IO](secretKey, clock, redisMock)
    }

  test(".createToken() - should generate a valid JWT token with userId, with expiry 1 hour from now") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      token <- tokenService.createToken(testUser, expiration)
      //      _ <- IO(println(s"Generated token: $token"))
      decodedClaim <- IO(JwtCirce.decode(token, secretKey, Seq(JwtAlgorithm.HS256)))
      //      _ <- IO(println(s"Decoding result: $decodedClaim"))
      //      _ <- IO(println(s"Decoded claim: ${decodedClaim.toOption.map(_.content)}"))
      userId <- IO {
        decodedClaim.toOption.flatMap { claim =>
          io.circe.parser.decode[Map[String, String]](claim.content).toOption.flatMap(_.get("userId"))
        }
      }
    } yield expect(userId.contains(testUser.userId))
  }

  test(".validateToken() - should return Some(userId) for a valid token") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      token <- tokenService.createToken(testUser, expiration)
      userIdOpt <- tokenService.validateToken(token)
    } yield expect(userIdOpt.contains(testUser.userId))
  }

  test(".validateToken() - should return None for a blacklisted token") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      token <- tokenService.createToken(testUser, expiration)
      _ <- tokenService.invalidateToken(token) // Add the token to the blacklist
      userIdOpt <- tokenService.validateToken(token)
    } yield expect(userIdOpt == Left(BlackListed))
  }

  test(".validateToken() - should return None for an invalid token") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)

    val invalidToken = "invalid.token.signature"

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      userIdOpt <- tokenService.validateToken(invalidToken)
    } yield expect(userIdOpt == Left(InvalidToken))
  }

  test(".invalidateToken() - should add token to blacklist with TTL") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)

    val invalidToken = JwtCirce.encode(Map("userId" -> testUser.userId).asJson.noSpaces, secretKey, JwtAlgorithm.HS256)

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      token <- tokenService.createToken(testUser, expiration)
      _ <- IO(println(s"Generated token: $token"))
      result <- tokenService.invalidateToken(token)
      blacklistCheck <- tokenService.validateToken(token)
    } yield
      expect.all(
        result,
        blacklistCheck == Left(BlackListed)
      )
  }

  test(".refreshAccessToken() - should generate a new token if refresh token is valid") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)

    val refreshToken = "valid-refresh-token"

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      _ <- tokenService.setRefreshToken(refreshToken, testUser.userId, 1.hour) // Use helper method to set refresh token
      newTokenOpt <- tokenService.refreshAccessToken(refreshToken)
    } yield expect(newTokenOpt.isDefined)
  }

  test(".refreshAccessToken() - should return None if refresh token is invalid") {

    val currentClock = Clock.systemUTC()
    val expiration = Instant.now(currentClock).plusSeconds(3600)
    val refreshToken = "invalid-refresh-token"

    for {
      tokenService <- createTokenService(secretKey, currentClock)
      newTokenOpt <- tokenService.refreshAccessToken(refreshToken)
    } yield expect(newTokenOpt.isEmpty)
  }
}

