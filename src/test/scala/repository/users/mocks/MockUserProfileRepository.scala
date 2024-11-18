package repository.users.mocks


import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.adts.Role
import models.users.wanderer_profile.profile.UserProfile
import repositories.users.UserProfileRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

case class MockUserProfileRepository(ref: Ref[IO, List[UserProfile]]) extends UserProfileRepositoryAlgebra[IO] {

  override def createUserProfile(user: UserProfile): IO[Int] =
    ref.modify(users => (user :: users, 1)) // Simulate inserting the user and returning success

  override def findByUsername(username: String): IO[Option[UserProfile]] =
    ref.get.map(_.find(_.userLoginDetails.username == username)) // Simulate finding the user by username

  override def findByContactNumber(contactNumber: String): IO[Option[UserProfile]] =
    ref.get.map(_.find(_.contactNumber.contains(contactNumber))) // Simulate finding the user by contact number

  override def findByEmail(email: String): IO[Option[UserProfile]] =
    ref.get.map(_.find(_.email.contains(email))) // Simulate finding the user by email

  override def findByUserId(userId: String): IO[Option[UserProfile]] =
    ref.get.map(_.find(_.userId == userId)) // Simulate finding the user by userId

  override def updateUserRole(userId: String, desiredRole: Role): IO[Option[UserProfile]] =
    ref.modify { users =>
      val updatedUsers = users.map {
        case user if user.userId == userId => user.copy(role = desiredRole)
        case user => user
      }
      (updatedUsers, updatedUsers.find(_.userId == userId))
    } // Simulate updating the user's role and returning the updated user
}