package services.auth.mocks

import cats.data.Validated
import cats.effect.IO
import models.users.{Role, UserProfile}
import repositories.users.UserProfileRepositoryAlgebra
import services.auth.algebra.PasswordServiceAlgebra
import services.auth.constants.AuthenticationServiceConstants.*

object AuthenticationServiceMocks {

  class MockUserRepository(
                            users: Map[String, UserProfile] = Map.empty
                          ) extends UserProfileRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, UserProfile]] = IO.pure(users)

    override def findByUsername(username: String): IO[Option[UserProfile]] = IO.pure(users.get(username))

    override def findByContactNumber(contactNumber: String): IO[Option[UserProfile]] = IO.pure(users.values.find(_.contact_number.contains(contactNumber)))

    override def findByEmail(email: String): IO[Option[UserProfile]] = IO.pure(users.values.find(_.email.contains(email)))

    override def createUserProfile(user: UserProfile): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def findByUserId(userId: String): IO[Option[UserProfile]] = ???

    override def updateUserRole(userId: String, desiredRole: Role): IO[Option[UserProfile]] = ???
  }


  class MockPasswordService(expectedHash: String) extends PasswordServiceAlgebra[IO] {

    override def validatePassword(plainTextPassword: String): Validated[List[String], String] =
      if (plainTextPassword.nonEmpty) Validated.valid(plainTextPassword) else Validated.invalid(List("Invalid password"))

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(expectedHash)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] =
      IO.pure(expectedHash == hashedPassword)
  }
}
