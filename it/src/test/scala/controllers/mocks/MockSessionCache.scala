package controllers.mocks

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.*
import cats.implicits.*
import dev.profunktor.redis4cats.RedisCommands
import doobie.util.transactor.Transactor
import infrastructure.SessionCacheAlgebra
import models.auth.UserSession
import models.cache.*
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.http4s.Uri
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.*
import services.*

class MockSessionCache(ref: Ref[IO, Map[String, UserSession]]) extends SessionCacheAlgebra[IO] {

  override def getSessionCookieOnly(userId: String): IO[Option[String]] = IO(Some("test-session-token"))

  override def lookupSession(token: String): IO[Option[UserSession]] = ???

  override def storeOnlyCookie(userId: String, token: String): IO[Unit] =
    ref.update(
      _ + (
        userId ->
          UserSession(
            userId = userId,
            cookieValue = token,
            email = s"$userId@gmail.com",
            userType = "Dev"
          )
      )
    )

  override def storeSession(userId: String, session: Option[UserSession]): IO[ValidatedNel[CacheErrors, CacheSuccess]] =
    session match {
      case Some(sess) =>
        ref.update(_ + (userId -> sess)) *>
          Sync[IO].pure(Valid(CacheUpdateSuccess))
      case None =>
        Sync[IO].pure(Invalid(CacheUpdateFailure).toValidatedNel)
    }

  override def getSession(userId: String): IO[Option[UserSession]] =
    ref.get.map(_.get(s"auth:session:$userId"))

  override def updateSession(userId: String, session: Option[UserSession]): IO[ValidatedNel[CacheErrors, CacheSuccess]] =
    ref
      .update(
        _.updated(
          s"auth:session:$userId",
          UserSession(
            userId = userId,
            cookieValue = session.map(_.cookieValue).getOrElse("no-cookie-available"),
            email = s"$userId@example.com",
            userType = "Dev"
          )
        )
      )
      .as(Validated.valid(CacheUpdateSuccess))

  override def deleteSession(userId: String): IO[Long] =
    ref.modify { current =>
      val removed = current - s"auth:session:$userId"
      val wasPresent = current.contains(s"auth:session:$userId")
      (removed, if (wasPresent) 1L else 0L)
    }
}
