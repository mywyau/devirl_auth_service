package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.address.BusinessAddress
import models.business.address.requests.CreateBusinessAddressRequest
import repository.business.mocks.MockBusinessAddressRepository

import java.time.LocalDateTime
import models.business.address.BusinessAddressPartial

object BusinessAddressRepositoryConstants {

  def createMockRepo(initialUsers: List[BusinessAddressPartial]): IO[MockBusinessAddressRepository] =
    Ref.of[IO, List[BusinessAddressPartial]](initialUsers).map(MockBusinessAddressRepository.apply)

  def testCreateBusinessAddressRequest(userId: String, businessId: String): CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
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
      longitude = Some(-100.1)
    )

  def testAddress(userId: String, businessId: String): BusinessAddressPartial =
    BusinessAddressPartial(
      userId = userId,
      businessId = businessId,
      buildingName = Some("building 1"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )


}
