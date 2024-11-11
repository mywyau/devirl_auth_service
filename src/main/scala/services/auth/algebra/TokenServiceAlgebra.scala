package services.auth.algebra

import models.users.wanderer_profile.profile.UserProfile
import services.auth.TokenStatus

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

trait TokenServiceAlgebra[F[_]] {

  def createToken(user: UserProfile, expiration: Instant): F[String]

  def validateToken(token: String): F[Either[TokenStatus , String]]

  def invalidateToken(token: String): F[Boolean]

  def refreshAccessToken(refreshToken: String): F[Option[String]]
}