package repositories

import cats.effect.Sync
import dev.profunktor.redis4cats.RedisCommands

import java.time.Instant
import scala.concurrent.duration.*
import java.time.Instant

trait RefreshTokenRepositoryAlgebra[F[_]] {

  def storeToken(token: String, userId: String, expiration: Instant): F[Unit]

  def findUserIdByToken(token: String): F[Option[String]]

  def revokeToken(token: String): F[Long]
}


class RedisRefreshTokenRepository[F[_]: Sync](redis: RedisCommands[F, String, String]) extends RefreshTokenRepositoryAlgebra[F] {

  /** Store the token in Redis with expiration. */
  override def storeToken(token: String, userId: String, expiration: Instant): F[Unit] = {
    val ttl = (expiration.getEpochSecond - Instant.now.getEpochSecond).seconds
    redis.setEx(token, userId, ttl)
  }

  /** Retrieve the user ID if the token is valid and has not expired. */
  override def findUserIdByToken(token: String): F[Option[String]] = redis.get(token)

  /** Revoke the token by deleting it from Redis. */
  override def revokeToken(token: String): F[Long] = redis.del(token)
}
