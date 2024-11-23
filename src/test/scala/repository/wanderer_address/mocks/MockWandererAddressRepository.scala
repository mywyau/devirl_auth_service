package repository.wanderer_address.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.wanderer.wanderer_address.service.WandererAddress
import repositories.user_profile.WandererAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockWandererAddressRepository(ref: Ref[IO, List[WandererAddress]]) extends WandererAddressRepositoryAlgebra[IO] {

  override def createUserAddress(wandererAddress: WandererAddress): IO[Int] =
    ref.modify(addresses => (wandererAddress :: addresses, 1))

  override def findByUserId(userId: String): IO[Option[WandererAddress]] =
    ref.get.map(_.find(_.userId == userId))

  override def updateAddressDynamic(
                                     userId: String,
                                     street: Option[String],
                                     city: Option[String],
                                     country: Option[String],
                                     county: Option[String],
                                     postcode: Option[String]
                                   ): IO[Option[WandererAddress]] =
    ref.modify { addresses =>
      addresses.find(_.userId == userId) match {
        case Some(existingAddress) =>
          val updatedAddress =
            existingAddress.copy(
              street = street,
              city = city,
              country = country,
              county = county,
              postcode = postcode,
              updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0) //TODO: Update timestamp to reflect the change and possibly test
            )
          (addresses.map {
            case addr if addr.userId == userId => updatedAddress
            case addr => addr
          }, Some(updatedAddress))
        case None =>
          (addresses, None) // No address found for the given userId
      }
    }

  override def createRegistrationWandererAddress(userId: String): IO[Int] =
    IO.pure(1)
}
