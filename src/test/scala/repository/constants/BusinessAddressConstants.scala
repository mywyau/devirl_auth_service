package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import repository.business.mocks.MockBusinessAddressRepository

import java.time.LocalDateTime

object BusinessAddressConstants {

  def createMockRepo(initialUsers: List[BusinessAddress]): IO[MockBusinessAddressRepository] =
    Ref.of[IO, List[BusinessAddress]](initialUsers).map(MockBusinessAddressRepository.apply)

  def testAddressRequest(userId: String, businessId: Option[String]): BusinessAddressRequest =
    BusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("mikeyCorp"),
      buildingName = Some("building 1"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def testAddress(id: Option[Int], userId: String, businessId: Option[String]): BusinessAddress =
    BusinessAddress(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = Some("mikeyCorp"),
      buildingName = Some("building 1"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


}
