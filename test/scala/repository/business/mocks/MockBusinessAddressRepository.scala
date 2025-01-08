package repository.business.mocks

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.kernel.Ref
import cats.effect.IO
import cats.implicits.*
import java.time.LocalDateTime
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.requests.UpdateBusinessAddressRequest
import models.business.address.BusinessAddressPartial
import models.database.*
import repositories.business.BusinessAddressRepositoryAlgebra

case class MockBusinessAddressRepository(ref: Ref[IO, List[BusinessAddressPartial]]) extends BusinessAddressRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[BusinessAddressPartial]] =
    ref.get.map(_.find(_.businessId.contains(businessId)))

  override def create(request: CreateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ref.modify(addresses =>
      (
        BusinessAddressPartial(
          request.userId,
          request.businessId,
          request.buildingName,
          request.floorNumber,
          request.street,
          request.city,
          request.country,
          request.county,
          request.postcode,
          request.latitude,
          request.longitude
        ) :: addresses,
        Valid(CreateSuccess)
      )
    )

  override def update(businessId: String, request: UpdateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ref.modify { addresses =>
      val (remainingAddresses, deletedCount) = addresses.partition(!_.businessId.contains(businessId)) match {
        case (remaining, deleted) if deleted.nonEmpty => (remaining, Valid(deleted.size))
        case (remaining, _) => (remaining, Validated.invalidNel(NotFoundError))
      }
      (remainingAddresses, Valid(DeleteSuccess))
    }

  override def deleteAllByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
}
