package services.auth.mocks

import cats.data.Validated
import cats.effect.IO
import models.auth.{PasswordLengthError, RegisterPasswordErrors}
import models.users.adts.Role
import models.users.wanderer_profile.profile.{UserLoginDetails, UserProfile}
import repositories.users.{UserLoginDetailsRepositoryAlgebra, UserProfileRepositoryAlgebra}
import services.auth.constants.AuthenticationServiceConstants.*
import services.password.PasswordServiceAlgebra

object AuthenticationServiceMocks {

  class MockUserLoginDetailsRepository(
                                        userLoginDetails: Map[String, UserLoginDetails] = Map.empty
                                      ) extends UserLoginDetailsRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, UserLoginDetails]] = IO.pure(userLoginDetails)

    override def findByUserId(userId: String): IO[Option[UserLoginDetails]] = ???

    override def createUserLoginDetails(user: UserLoginDetails): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def findByUsername(username: String): IO[Option[UserLoginDetails]] = IO.pure(userLoginDetails.get(username))

    override def findByEmail(email: String): IO[Option[UserLoginDetails]] = IO.pure(userLoginDetails.values.find(_.email.contains(email)))

    def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): IO[Option[UserLoginDetails]] = ???
  }

  class MockUserProfileRepository(
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

    override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] =
      if (plainTextPassword.nonEmpty) Validated.valid(plainTextPassword) else Validated.invalid(List(PasswordLengthError))

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(expectedHash)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] =
      IO.pure(expectedHash == hashedPassword)
  }
}
