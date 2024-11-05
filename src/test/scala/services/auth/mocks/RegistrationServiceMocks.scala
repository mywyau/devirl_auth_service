package services.auth.mocks

import cats.data.Validated
import cats.effect.IO
import models.users.*
import models.users.database.UserLoginDetails
import repositories.users.{UserLoginDetailsRepositoryAlgebra, UserProfileRepositoryAlgebra}
import services.auth.algebra.PasswordServiceAlgebra
import services.auth.constants.RegistrationServiceConstants.*

import java.time.LocalDateTime

object RegistrationServiceMocks {

  class MockUserLoginDetailsRepository(
                            existingUserLoginDetails: Map[String, UserLoginDetails] = Map.empty
                          ) extends UserLoginDetailsRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, UserLoginDetails]] = IO.pure(existingUserLoginDetails)

    override def createUserLoginDetails(user: UserLoginDetails): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def findByUserId(userId: String): IO[Option[UserLoginDetails]] = IO.pure(existingUserLoginDetails.get(userId))

    override def findByUsername(username: String): IO[Option[UserLoginDetails]] = IO.pure(existingUserLoginDetails.get(username))
    
    override def findByEmail(email: String): IO[Option[UserLoginDetails]] = IO.pure(existingUserLoginDetails.values.find(_.email.contains(email)))
    
    override def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): IO[Option[UserLoginDetails]] = ???
  }

  class MockPasswordService(
                             passwordValidationResult: Validated[List[String], String],
                             hashedPassword: String
                           ) extends PasswordServiceAlgebra[IO] {
    override def validatePassword(plainTextPassword: String): Validated[List[String], String] = passwordValidationResult

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(hashedPassword)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] = IO.pure(true)
  }
}
