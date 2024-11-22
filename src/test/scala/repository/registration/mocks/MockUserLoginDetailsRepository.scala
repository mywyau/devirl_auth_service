package repository.registration.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.adts.{Role, Wanderer}
import models.users.wanderer_profile.profile.UserLoginDetails
import repositories.user_profile.UserLoginDetailsRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockUserLoginDetailsRepository(ref: Ref[IO, Map[String, UserLoginDetails]]) extends UserLoginDetailsRepositoryAlgebra[IO] {

  override def createUserLoginDetails(user: UserLoginDetails): IO[Int] =
    ref.modify { users =>
      if (users.contains(user.userId)) users -> 0 // User already exists
      else users.updated(user.userId, user) -> 1
    }

  override def findByUserId(userId: String): IO[Option[UserLoginDetails]] =
    ref.get.map(_.get(userId))

  override def findByUsername(username: String): IO[Option[UserLoginDetails]] =
    ref.get.map(_.values.find(_.username == username))

  override def findByEmail(email: String): IO[Option[UserLoginDetails]] =
    ref.get.map(_.values.find(_.email == email))

  override def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): IO[Option[UserLoginDetails]] =
    ref.modify { users =>
      users.get(userId) match {
        case Some(_) => users.updated(userId, userLoginDetails) -> Some(userLoginDetails)
        case None => users -> None
      }
    }

  override def updateUserLoginDetailsDynamic(
                                              userId: String,
                                              username: Option[String],
                                              passwordHash: Option[String],
                                              email: Option[String],
                                              role: Option[Role]
                                            ): IO[Option[UserLoginDetails]] =
    ref.modify { users =>
      users.get(userId) match {
        case Some(existingUser) =>
          val updatedUser = existingUser.copy(
            username = username.getOrElse(existingUser.username),
            passwordHash = passwordHash.getOrElse(existingUser.passwordHash),
            email = email.getOrElse(existingUser.email),
            role = role.getOrElse(existingUser.role)
          )
          users.updated(userId, updatedUser) -> Some(updatedUser)
        case None =>
          users -> None
      }
    }
}