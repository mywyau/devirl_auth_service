package services.users

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.User
import repositories.UserRepositoryAlgebra

case class MockUserRepository(ref: Ref[IO, List[User]]) extends UserRepositoryAlgebra[IO] {
  override def findByUsername(username: String): IO[Option[User]] =
    ref.get.map(_.find(_.username == username))

  override def findByContactNumber(contactNumber: String): IO[Option[User]] =
    ref.get.map(_.find(_.contact_number == contactNumber))

  override def findByEmail(email: String): IO[Option[User]] =
    ref.get.map(_.find(_.email == email))

  override def createUser(user: User): IO[Int] =
    ref.modify(users => (user :: users, 1)) // Simulate user creation by adding to the list
}