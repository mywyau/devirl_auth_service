package controllers

import infrastructure.cache.*
import infrastructure.cache.SessionCacheAlgebra
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.effect.kernel.Async
import cats.implicits.*
import fs2.Stream
import io.circe.Json
import io.circe.syntax.EncoderOps
import models.database.UpdateSuccess
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.GetResponse
import models.responses.UpdatedResponse
import models.users.CreateUserData
import models.users.UpdateUserData
import models.users.Registration
import org.http4s.*
import org.http4s.Challenge
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`WWW-Authenticate`
import org.http4s.syntax.all.http4sHeaderSyntax
import org.typelevel.log4cats.Logger
import services.UserDataServiceAlgebra

import scala.concurrent.duration.*

trait UserDataControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class UserDataControllerImpl[F[_] : Async : Concurrent : Logger](
  userService: UserDataServiceAlgebra[F],
  sessionCache: SessionCacheAlgebra[F]
) extends Http4sDsl[F]
    with UserDataControllerAlgebra[F] {

  implicit val updateUserTypeDecoder: EntityDecoder[F, Registration] = jsonOf[F, Registration]
  implicit val updateUserDataDecoder: EntityDecoder[F, UpdateUserData] = jsonOf[F, UpdateUserData]
  implicit val createUserDataDecoder: EntityDecoder[F, CreateUserData] = jsonOf[F, CreateUserData]

  private def extractCookieSessionToken(req: Request[F]): Option[String] =
    req.cookies
      .find(_.name == "auth_session")
      .map(_.content)

  private def withValidSession(userId: String, token: String)(onValid: F[Response[F]]): F[Response[F]] =
    Logger[F].debug(s"[UserDataControllerImpl][withValidSession] UserId: $userId, token: $token") *>
      sessionCache.getSession(userId).flatMap {
        case Some(userSession) if userSession.cookieValue == token =>
          Logger[F].debug(s"[UserDataControllerImpl][withValidSession] User session: $userSession") *>
            onValid
        case Some(session) =>
          Logger[F].debug(s"[UserDataControllerImpl][withValidSession] User session does not match request user session token value from redis. $session") *>
            Forbidden("User session does not match request user session token value from redis.")
        case None =>
          Logger[F].debug("[UserDataControllerImpl][withValidSession] Invalid or expired session")
          Forbidden("Invalid or expired session")
      }

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ GET -> Root / "user" / "health" =>
      Logger[F].debug(s"[BaseControllerImpl] GET - Health check for backend UserDataController service") *>
        Ok(GetResponse("/dev-user-service/health", "I am alive").asJson)

    case req @ GET -> Root / "user" / "data" / userId =>
      (
        Logger[F].debug(s"[UserDataController][/user/data/$userId] GET - Attempting to retrieve user details") *>
          Async[F].pure(extractCookieSessionToken(req))
      ).flatMap {
        case Some(cookieToken) =>
          withValidSession(userId, cookieToken) {
            Logger[F].debug(s"[UserDataController] GET - Authenticated for userId $userId") *>
              userService.getUser(userId).flatMap {
                case Some(user) =>
                  Logger[F].debug(s"[UserDataController] GET - Found user ${userId.toString()}") *>
                    Ok(user.asJson)
                case None =>
                  BadRequest(ErrorResponse("No_User_Data", "No user found").asJson)
              }
          }
        case None =>
          Logger[F].debug(s"[UserDataController] GET - Unauthorised") *>
            Unauthorized(`WWW-Authenticate`(Challenge("Bearer", "api")), "Missing Cookie")
      }

    case req @ POST -> Root / "user" / "data" / "create" / userId =>
      extractCookieSessionToken(req) match {
        case Some(headerToken) =>
          withValidSession(userId, headerToken) {
            Logger[F].debug(s"[UserDataControllerImpl] POST - Creating user") *>
              req.decode[CreateUserData] { request =>
                userService.createUser(userId, request).flatMap {
                  case Valid(response) =>
                    Logger[F].debug(s"[UserDataControllerImpl] POST - Successfully created a user") *>
                      Created(CreatedResponse(response.toString, "user details created successfully").asJson)
                  case Invalid(_) =>
                    InternalServerError(ErrorResponse(code = "Code", message = "An error occurred").asJson)
                }
              }
          }
        case None =>
          Unauthorized(`WWW-Authenticate`(Challenge("Bearer", "api")), "Missing Cookie")
      }

    case req @ PUT -> Root / "user" / "data" / "update" / userId =>
      extractCookieSessionToken(req) match {
        case Some(headerToken) =>
          withValidSession(userId, headerToken) {
            Logger[F].debug(s"[UserDataControllerImpl] PUT - Updating user with ID: $userId") *>
              req.decode[UpdateUserData] { request =>
                userService.updateUserData(userId, request).flatMap {
                  case Valid(response) =>
                    Logger[F].debug(s"[UserDataControllerImpl] PUT - Successfully updated user for ID: $userId") *>
                      Ok(UpdatedResponse(UpdateSuccess.toString, s"User $userId updated successfully").asJson)
                  case Invalid(errors) =>
                    Logger[F].debug(s"[UserDataControllerImpl] PUT - Validation failed for user update: ${errors.toList}") *>
                      BadRequest(ErrorResponse(code = "VALIDATION_ERROR", message = errors.toList.mkString(", ")).asJson)
                }
              }
          }
        case None =>
          Unauthorized(`WWW-Authenticate`(Challenge("Bearer", "api")), "Missing Cookie")
      }

    case req @ DELETE -> Root / "user" / "data" / "delete" / userId =>
      extractCookieSessionToken(req) match {
        case Some(headerToken) =>
          withValidSession(userId, headerToken) {
            Logger[F].debug(s"[UserDataControllerImpl] DELETE - Attempting to delete user") *>
              userService.deleteUser(userId).flatMap {
                case Valid(response) =>
                  Logger[F].debug(s"[UserDataControllerImpl] DELETE - Successfully deleted user for $userId") *>
                    Ok(DeletedResponse(response.toString, "User deleted successfully").asJson)
                case Invalid(error) =>
                  val errorResponse = ErrorResponse("placeholder error", "some deleted user message")
                  BadRequest(errorResponse.asJson)
              }
          }
        case None =>
          Unauthorized(`WWW-Authenticate`(Challenge("Bearer", "api")), "Missing cookie token")
      }
  }
}

object UserDataController {
  def apply[F[_] : Async : Concurrent](
    userService: UserDataServiceAlgebra[F],
    sessionCache: SessionCacheAlgebra[F]
  )(implicit logger: Logger[F]): UserDataControllerAlgebra[F] =
    new UserDataControllerImpl[F](userService, sessionCache)
}
