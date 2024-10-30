package services.auth.mocks

import cats.data.Validated
import cats.effect.IO
import models.users.User
import repositories.UserRepositoryAlgebra
import services.PasswordServiceAlgebra
import services.auth.constants.AuthenticationServiceConstants.*

object AuthenticationServiceMocks {

  class MockUserRepository(
                            users: Map[String, User] = Map.empty
                          ) extends UserRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, User]] = IO.pure(users)

    override def findByUsername(username: String): IO[Option[User]] = IO.pure(users.get(username))

    override def findByContactNumber(contactNumber: String): IO[Option[User]] = IO.pure(users.values.find(_.contact_number == contactNumber))

    override def findByEmail(email: String): IO[Option[User]] = IO.pure(users.values.find(_.email == email))

    override def createUser(user: User): IO[Int] = IO.pure(1) // Assume user creation always succeeds
  }


  class MockPasswordService(expectedHash: String) extends PasswordServiceAlgebra[IO] {

    override def validatePassword(plainTextPassword: String): Validated[List[String], String] =
      if (plainTextPassword.nonEmpty) Validated.valid(plainTextPassword) else Validated.invalid(List("Invalid password"))

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(expectedHash)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] =
      IO.pure(expectedHash == hashedPassword)
  }
}
