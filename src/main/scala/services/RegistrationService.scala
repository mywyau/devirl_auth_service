package services

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.all.*
import cats.Monad
import cats.NonEmptyParallel
import fs2.Stream
import java.time.Instant
import java.util.UUID
import kafka.*
import kafka.events.*
import models.database.*
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.users.*
import models.UserType
import org.typelevel.log4cats.Logger
import repositories.UserDataRepositoryAlgebra

trait RegistrationServiceAlgebra[F[_]] {

  def registerUser(userId: String, registrationData: RegistrationData): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteUser(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class RegistrationServiceImpl[F[_] : Concurrent : Monad : Logger](
  userRepo: UserDataRepositoryAlgebra[F],
  registrationEventProducer: RegistrationEventProducerAlgebra[F]
) extends RegistrationServiceAlgebra[F] {

  override def registerUser(userId: String, registrationData: RegistrationData): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    userRepo.registerUser(userId, registrationData).flatTap {

      case Valid(_) =>
        val event =
          UserRegisteredEvent(
            userId = userId,
            username = registrationData.username,
            email = registrationData.email,
            userType = registrationData.userType, // assuming this is a UserRole-like type
            createdAt = Instant.now()
          )

        // 📨 Publish event to Kafka
        registrationEventProducer.publishUserRegistrationDataCreated(event).flatMap {
          case SuccessfulWrite =>
            Logger[F].info(s"[RegistrationService][registerUser] - Published UserRegisteredEvent for user $userId")
          case FailedWrite(err, None) =>
            Logger[F].error(s"[RegistrationService][registerUser] - Failed to publish UserRegisteredEvent for user $userId: $err")
          case _ =>
            Logger[F].error(s"[RegistrationService][registerUser] - Unknown error - failed to publish UserRegisteredEvent for user $userId")
        }

      case Invalid(errors) =>
        Logger[F].warn(s"[RegistrationService] Registration failed for user $userId. Errors: ${errors.toList.mkString(", ")}")
    }

  override def deleteUser(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    userRepo.deleteUser(userId).flatTap {
      case Valid(_) =>
        Logger[F].info(s"[RegistrationService] Successfully deleted user $userId")
      case Invalid(errors) =>
        Logger[F].error(s"[RegistrationService] Failed to delete user $userId. Errors: ${errors.toList.mkString(", ")}")
    }
}

object RegistrationService {
  def apply[F[_] : Concurrent : NonEmptyParallel : Logger](
    userRepo: UserDataRepositoryAlgebra[F],
    eventProducer: RegistrationEventProducerAlgebra[F]
  ): RegistrationServiceAlgebra[F] =
    new RegistrationServiceImpl[F](userRepo, eventProducer)
}
