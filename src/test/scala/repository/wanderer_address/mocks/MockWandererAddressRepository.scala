package repository.wanderer_address.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.wanderer_address.service.WandererAddress
import repositories.users.WandererAddressRepositoryAlgebra

case class MockWandererAddressRepository(ref: Ref[IO, List[WandererAddress]]) extends WandererAddressRepositoryAlgebra[IO] {

  override def createUserAddress(wandererAddress: WandererAddress): IO[Int] =
    ref.modify(addresses => (wandererAddress :: addresses, 1))

  override def findByUserId(userId: String): IO[Option[WandererAddress]] =
    ref.get.map(_.find(_.user_id == userId))
}