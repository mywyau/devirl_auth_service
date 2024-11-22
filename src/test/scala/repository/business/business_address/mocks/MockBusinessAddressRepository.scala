package repository.business.business_address.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.business.business_address.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockBusinessAddressRepository(ref: Ref[IO, List[BusinessAddress]]) extends BusinessAddressRepositoryAlgebra[IO] {

  override def createUserAddress(BusinessAddress: BusinessAddress): IO[Int] =
    ref.modify(addresses => (BusinessAddress :: addresses, 1))

  override def findByUserId(userId: String): IO[Option[BusinessAddress]] =
    ref.get.map(_.find(_.userId == userId))

  override def updateAddressDynamic(
                                     userId: String,
                                     street: Option[String],
                                     city: Option[String],
                                     country: Option[String],
                                     county: Option[String],
                                     postcode: Option[String]
                                   ): IO[Option[BusinessAddress]] =
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

  override def createRegistrationBusinessAddress(userId: String): IO[Int] =
    IO.pure(1)
}
