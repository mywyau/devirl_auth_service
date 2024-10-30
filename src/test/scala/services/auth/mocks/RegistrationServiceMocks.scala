package services.auth.mocks

import cats.data.Validated
import cats.effect.IO
import models.users.*
import repositories.UserRepositoryAlgebra
import services.PasswordServiceAlgebra
import services.auth.constants.RegistrationServiceConstants.*

import java.time.LocalDateTime

object RegistrationServiceMocks {

  class MockUserRepository(
                            existingUsers: Map[String, User] = Map.empty
                          ) extends UserRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, User]] = IO.pure(existingUsers)

    override def findByUsername(username: String): IO[Option[User]] = IO.pure(existingUsers.get(username))

    override def findByContactNumber(contactNumber: String): IO[Option[User]] = IO.pure(existingUsers.values.find(_.contact_number == contactNumber))

    override def findByEmail(email: String): IO[Option[User]] = IO.pure(existingUsers.values.find(_.email == email))

    override def createUser(user: User): IO[Int] = IO.pure(1) // Assume user creation always succeeds
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
