package repository.business.mocks

import cats.data.ValidatedNel
import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.address_details.service.BusinessAddress
import models.business.address_details.requests.BusinessAddressRequest
import models.database.SqlErrors
import repositories.business.BusinessAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockBusinessAddressRepository(ref: Ref[IO, List[BusinessAddress]]) extends BusinessAddressRepositoryAlgebra[IO] {

  override def findByBusinessId(userId: String): IO[Option[BusinessAddress]] =
    ref.get.map(_.find(_.userId == userId))

  override def createBusinessAddress(request: BusinessAddressRequest): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify(addresses => (
      BusinessAddress(
        Some(1),
        request.userId,
        request.businessId,
        request.businessName,
        request.buildingName,
        request.floorNumber,
        request.street,
        request.city,
        request.country,
        request.county,
        request.postcode,
        request.latitude,
        request.longitude,
        request.createdAt,
        request.updatedAt
      )
        :: addresses, Valid(1))
    )

}
