package services

import cats.Monad
import cats.NonEmptyParallel
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.all.*
import fs2.Stream
import models.UserType
import models.database.*
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.users.*
import org.typelevel.log4cats.Logger
import repositories.UserDataRepositoryAlgebra

import java.util.UUID

trait RegistrationServiceAlgebra[F[_]] {

  def registerUser(userId: String, registrationData: RegistrationData): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteUser(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class RegistrationServiceImpl[F[_] : Concurrent : Monad : Logger](
  userRepo: UserDataRepositoryAlgebra[F]
) extends RegistrationServiceAlgebra[F] {

  override def registerUser(userId: String, registrationData: RegistrationData): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    userRepo.registerUser(userId, registrationData).flatMap {
      case Valid(value) =>
        Logger[F].debug(s"[UserDataService][updateUserData] Successfully updated user with ID: $userId") *>
          Concurrent[F].pure(Valid(value))
      case Invalid(errors) =>
        Logger[F].error(s"[UserDataService][updateUserData] Failed to update user with ID: $userId. Errors: ${errors.toList.mkString(", ")}") *>
          Concurrent[F].pure(Invalid(errors))
    }

  override def deleteUser(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    userRepo.deleteUser(userId).flatMap {
      case Valid(value) =>
        Logger[F].debug(s"[UserDataService] Successfully deleted user with ID: $userId") *>
          Concurrent[F].pure(Valid(value))
      case Invalid(errors) =>
        Logger[F].error(s"[UserDataService] Failed to delete user with ID: $userId. Errors: ${errors.toList.mkString(", ")}") *>
          Concurrent[F].pure(Invalid(errors))
    }
}

object RegistrationService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Logger](userRepo: UserDataRepositoryAlgebra[F]): RegistrationServiceAlgebra[F] =
    new RegistrationServiceImpl[F](userRepo)
}
