package services.auth

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import models.users.UserProfile
import repositories.RefreshTokenRepositoryAlgebra
import repositories.users.UserProfileRepositoryAlgebra
import services.auth.algebra.TokenServiceAlgebra

import java.time.Instant
import java.util.UUID

trait SessionManagerAlgebra[F[_]] {

  /** Generate access and refresh tokens for a user */
  def generateTokens(user: UserProfile): F[(String, String)]

  /** Refresh an access token using a refresh token */
  def refreshAccessToken(username: String, refreshToken: String): F[Option[String]]

  /** Revoke a refresh token to end a session */
  def revokeSession(refreshToken: String): F[Long]
}

class SessionManager[F[_] : Concurrent : Monad](
                                                 tokenService: TokenServiceAlgebra[F],
                                                 refreshTokenRepo: RefreshTokenRepositoryAlgebra[F],
                                                 userRepository: UserProfileRepositoryAlgebra[F]
                                               ) extends SessionManagerAlgebra[F] {

  // Generate access and refresh tokens for a user
  override def generateTokens(user: UserProfile): F[(String, String)] = {
    for {
      accessToken <- tokenService.createToken(user, Instant.now.plusSeconds(900)) // 15 minutes expiration
      refreshToken = UUID.randomUUID().toString
      _ <- refreshTokenRepo.storeToken(refreshToken, user.userId, Instant.now.plusSeconds(604800)) // 7 days expiration
    } yield (accessToken, refreshToken)
  }

  // Refresh an access token using a refresh token
  // change this to be based on userId
  override def refreshAccessToken(username: String, refreshToken: String): F[Option[String]] = {
    userRepository.findByUsername(username).flatMap {
      case Some(user) =>
        refreshTokenRepo.findUserIdByToken(refreshToken).flatMap {
          case Some(userId) if userId == user.userId =>
            // If the user ID matches, generate a new access token
            tokenService.createToken(user, Instant.now.plusSeconds(900)).map(Some(_))
          case _ =>
            Concurrent[F].pure(None) // Invalid refresh token or mismatched user ID
        }
      case None =>
        Concurrent[F].pure(None) // User not found
    }
  }

  // Revoke a refresh token to end a session
  override def revokeSession(refreshToken: String): F[Long] = refreshTokenRepo.revokeToken(refreshToken)
}
