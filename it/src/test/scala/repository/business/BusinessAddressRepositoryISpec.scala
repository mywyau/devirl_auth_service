package repository.business

import cats.data.Validated.Valid
import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.address.BusinessAddress
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.adts.PrivateDesk
import models.business.specifications.BusinessAvailability
import repositories.business.BusinessAddressRepositoryImpl
import repository.fragments.business.BusinessAddressRepoFragments.{createBusinessAddressTable, insertBusinessAddressData, resetBusinessAddressTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class BusinessAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessAddressTable.update.run.transact(transactor.xa).void *>
        resetBusinessAddressTable.update.run.transact(transactor.xa).void *>
        insertBusinessAddressData.update.run.transact(transactor.xa).void
    )

  def testBusinessAddressRequest(userId: String, businessId: String): CreateBusinessAddressRequest = {
    CreateBusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("mikey_corp"),
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("fake county"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )
  }

  def sharedResource: Resource[IO, BusinessAddressRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessAddressRepo = new BusinessAddressRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessAddressRepo

    setup
  }

  test(".findByBusinessId() - should find and return the business address if business_id exists for a previously created business address") { businessAddressRepo =>

    val expectedResult =
      BusinessAddress(
        id = Some(1),
        userId = "USER001",
        businessId = "BUS001",
        businessName = Some("Tech Innovations"),
        buildingName = Some("Innovation Tower"),
        floorNumber = Some("5"),
        street = Some("123 Tech Street"),
        city = Some("San Francisco"),
        country = Some("USA"),
        county = Some("California"),
        postcode = Some("94105"),
        latitude = Some(37.774929),
        longitude = Some(-122.419416),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessAddressOpt <- businessAddressRepo.findByBusinessId("BUS001")
      //      _ <- IO(println(s"Query Result: $businessAddressOpt")) // Debug log the result
    } yield expect(businessAddressOpt == Some(expectedResult))
  }

  test(".deleteBusinessAddress() - should delete the business address if business_id exists for a previously existing business address") { businessAddressRepo =>

    val userId = "USER002"
    val businessId = "BUS002"

    val expectedResult =
      BusinessAddress(
        id = Some(2),
        userId = userId,
        businessId = businessId,
        businessName = Some("Global Corp"),
        buildingName = Some("Global Tower"),
        floorNumber = Some("12"),
        street = Some("456 Global Ave"),
        city = Some("New York"),
        country = Some("USA"),
        county = Some("New York"),
        postcode = Some("10001"),
        latitude = Some(40.712776),
        longitude = Some(-74.005974),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      firstFindResult <- businessAddressRepo.findByBusinessId(businessId)
      deleteResult <- businessAddressRepo.deleteBusinessAddress(businessId)
      afterDeletionFindResult <- businessAddressRepo.findByBusinessId(businessId)
    } yield expect.all(
      firstFindResult == Some(expectedResult),
      deleteResult == Valid(1),
      afterDeletionFindResult == None
    )
  }
}
