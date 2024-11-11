package services.auth

import cats.Applicative
import cats.effect.{Concurrent, IO}
import cats.syntax.all.*
import dev.profunktor.redis4cats.RedisCommands
import io.circe.parser.decode
import io.circe.syntax.*
import models.users.wanderer_profile.profile.UserProfile
import pdi.jwt.exceptions.JwtExpirationException
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import services.auth.algebra.TokenServiceAlgebra

import java.time.{Clock, Instant}
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}


sealed trait TokenStatus

case object BlackListed extends TokenStatus
case object ValidToken extends TokenStatus
case object InvalidToken extends TokenStatus
case object ExpiredToken extends TokenStatus

// Define a minimal Redis-like trait with only the methods needed for TokenServiceImpl
trait TokenRedisCommands[F[_]] {

  def get(key: String): F[Option[String]]

  def setEx(key: String, value: String, ttl: FiniteDuration): F[Boolean]
}

// Redis-based implementation of TokenRedisCommands
class RedisTokenCommands[F[_] : Applicative](redis: RedisCommands[F, String, String]) extends TokenRedisCommands[F] {

  override def get(key: String): F[Option[String]] =
    redis.get(key)

  override def setEx(key: String, value: String, ttl: FiniteDuration): F[Boolean] =
    redis.setEx(key, value, ttl).map(_ => true)
}


class TokenServiceImpl[F[_] : Concurrent](secretKey: String, clock: Clock, redis: TokenRedisCommands[F]) extends TokenServiceAlgebra[F] {

  private val blacklistPrefix = "blacklist:"

  private val refreshTokenPrefix = "refreshToken:"

  private def decodeTokenExpiration(token: String): F[Option[Instant]] = {
    Concurrent[F].pure {
      JwtCirce.decode(token, secretKey, Seq(JwtAlgorithm.HS256)).toOption
        .flatMap(_.expiration.map(Instant.ofEpochSecond))
    }
  }

  override def createToken(user: UserProfile, expiration: Instant): F[String] = {
    val claim =
      JwtClaim(
        content = Map("userId" -> user.userId).asJson.noSpaces,
        expiration = Some(expiration.getEpochSecond)
      ).issuedNow(clock)

    Concurrent[F].pure(JwtCirce.encode(claim, secretKey, JwtAlgorithm.HS256))
  }

  override def validateToken(token: String): F[Either[TokenStatus, String]] = {
    // First, check if the token is blacklisted
    redis.get(blacklistPrefix + token).flatMap {
      case Some(_) =>
        // Token is blacklisted
        Concurrent[F].pure(Left(BlackListed))
      case None =>
        // If not blacklisted, decode and validate token
        JwtCirce.decode(token, secretKey, Seq(JwtAlgorithm.HS256)) match {
          case Success(decodedClaim) =>
            // Decode the content to extract userId if valid
            decode[Map[String, String]](decodedClaim.content) match {
              case Right(data) =>
                data.get("userId") match {
                  case Some(userId) => Concurrent[F].pure(Right(userId))
                  case None => Concurrent[F].pure(Left(InvalidToken)) // No userId in token
                }
              case Left(_) =>
                // Content decoding failure means invalid token
                Concurrent[F].pure(Left(InvalidToken))
            }
          case Failure(e: JwtExpirationException) =>
            // Token has expired
            Concurrent[F].pure(Left(ExpiredToken))
          case Failure(_) =>
            // General decoding failure means invalid token
            Concurrent[F].pure(Left(InvalidToken))
        }
    }
  }


  override def invalidateToken(token: String): F[Boolean] = {
    decodeTokenExpiration(token).flatMap {
      case Some(expiration) =>
        // Calculate TTL based on token's expiration time
        val ttl = FiniteDuration(expiration.getEpochSecond - Instant.now(clock).getEpochSecond, "seconds")
        println(blacklistPrefix + token)
        redis.setEx(key = blacklistPrefix + token, value = "blacklisted", ttl = ttl).as(true)
      case None =>
        Concurrent[F].pure(false) // Token was invalid, nothing to blacklist
    }
  }

  override def refreshAccessToken(refreshToken: String): F[Option[String]] = {
    redis.get(refreshTokenPrefix + refreshToken).flatMap {
      case Some(userId) =>
        // Generate a new access token
        val expiration = Instant.now(clock).plusSeconds(3600) // 1 hour expiration
        val newAccessToken =
          JwtClaim(
            content = Map("userId" -> userId).asJson.noSpaces,
            expiration = Some(expiration.getEpochSecond)
          ).issuedNow(clock)
        Concurrent[F].pure(Some(JwtCirce.encode(newAccessToken, secretKey, JwtAlgorithm.HS256)))
      case None => Concurrent[F].pure(None) // Refresh token invalid or expired
    }
  }

  def setRefreshToken(refreshToken: String, userId: String, ttl: FiniteDuration): F[Unit] =
    redis.setEx(s"refreshToken:$refreshToken", userId, ttl).void
}
