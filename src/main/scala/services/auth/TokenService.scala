package services.auth

import cats.effect.Concurrent
import io.circe.parser.decode
import io.circe.syntax.*
import models.users.User
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import java.time.{Clock, Instant}

trait TokenServiceAlgebra[F[_]] {

  def createToken(user: User, expiration: Instant): F[String]

  def validateToken(token: String): F[Option[String]]
}

class TokenServiceImpl[F[_] : Concurrent](secretKey: String, clock: Clock) extends TokenServiceAlgebra[F] {

  override def createToken(user: User, expiration: Instant): F[String] = {
    val claim = JwtClaim(
      content = Map("userId" -> user.userId).asJson.noSpaces,
      expiration = Some(expiration.getEpochSecond)
    ).issuedNow(clock)

    Concurrent[F].pure(JwtCirce.encode(claim, secretKey, JwtAlgorithm.HS256))
  }

  // Decode and validate JWT, returning the userId if valid
  override def validateToken(token: String): F[Option[String]] = {
    Concurrent[F].pure {
      JwtCirce.decode(token, secretKey, Seq(JwtAlgorithm.HS256)).toOption
        .flatMap { decodedClaim =>
          decode[Map[String, String]](decodedClaim.content).toOption.flatMap(_.get("userId"))
        }
    }
  }
}
