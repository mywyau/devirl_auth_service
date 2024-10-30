package services.auth

import cats.effect.IO
import models.users.User
import pdi.jwt.{JwtAlgorithm, JwtCirce}
import weaver.SimpleIOSuite

import java.time.{Clock, Instant, LocalDateTime, ZoneOffset}

object TokenServiceSpec extends SimpleIOSuite {

  val fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)

  // Sample data and setup
  val testUser =
    User(
      "user_id_1",
      "username",
      "hashed_password",
      "John",
      "Doe",
      "07402205071",
      "john@example.com",
      models.users.Admin,
      LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val secretKey = "supersecretkey"
  val tokenService = new TokenServiceImpl[IO](secretKey, fixedClock)

  test(".createToken() - should generate a valid JWT token with userId") {
    val expiration = Instant.now.plusSeconds(3600) // 1 hour from now

    for {
      token <- tokenService.createToken(testUser, expiration)
      decodedClaim <- IO(JwtCirce.decode(token, secretKey, Seq(JwtAlgorithm.HS256)))
      userId <- IO(decodedClaim.toOption.flatMap(claim => io.circe.parser.decode[Map[String, String]](claim.content).toOption.flatMap(_.get("userId"))))
    } yield expect(userId.contains(testUser.userId))
  }

  test(".validateToken() - should return Some(userId) for a valid token") {
    val expiration = Instant.now.plusSeconds(3600)

    for {
      token <- tokenService.createToken(testUser, expiration)
      userIdOpt <- tokenService.validateToken(token)
    } yield expect(userIdOpt.contains(testUser.userId))
  }

  test(".validateToken() - should return None for an invalid token") {
    val invalidToken = "invalid.token.signature"

    for {
      userIdOpt <- tokenService.validateToken(invalidToken)
    } yield expect(userIdOpt.isEmpty)
  }

  test("validateToken should return None for an expired token") {
    val expiration = Instant.now.minusSeconds(3600) // 1 hour ago (expired)

    for {
      token <- tokenService.createToken(testUser, expiration)
      userIdOpt <- tokenService.validateToken(token)
    } yield expect(userIdOpt.isEmpty)
  }
}
