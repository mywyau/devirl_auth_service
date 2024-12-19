package repository.business

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.business.business_address.service.BusinessAddress
import models.business.business_address.requests.BusinessAddressRequest
import models.business.specifications.BusinessAvailability
import repositories.business.BusinessAddressRepositoryImpl
import repository.fragments.business.BusinessAddressRepoFragments.{createBusinessAddressTable, resetBusinessAddressTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class BusinessAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessAddressTable.update.run.transact(transactor.xa).void *>
        resetBusinessAddressTable.update.run.transact(transactor.xa).void
    )

  def testBusinessAddressRequest(userId: String, businessId: Option[String]): BusinessAddressRequest = {
    BusinessAddressRequest(
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
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  private def seedTestAddresses(businessAddressRepo: BusinessAddressRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testBusinessAddressRequest("user_id_1", Some("business_id_1")),
      testBusinessAddressRequest("user_id_2", Some("business_id_2")),
      testBusinessAddressRequest("user_id_3", Some("business_id_3")),
      testBusinessAddressRequest("user_id_4", Some("business_id_4")),
      testBusinessAddressRequest("user_id_5", Some("business_id_5"))
    )
    users.traverse(businessAddressRepo.createBusinessAddress).void
  }

  def sharedResource: Resource[IO, BusinessAddressRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessAddressRepo = new BusinessAddressRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestAddresses(businessAddressRepo))
    } yield businessAddressRepo

    setup
  }

  test(".findByBusinessId() - should return the business address if business_id exists for a previously created business address") { businessAddressRepo =>

    val expectedResult =
      BusinessAddress(
        id = Some(1),
        userId = "user_id_1",
        businessName = Some("mikey_corp"),
        businessId = Some("business_id_1"),
        buildingName = Some("build_123"),
        floorNumber = Some("floor 1"),
        street = Some("123 Main Street"),
        city = Some("New York"),
        country = Some("USA"),
        county = Some("fake county"),
        postcode = Some("10001"),
        latitude = Some(100.1),
        longitude = Some(-100.1),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessAddressOpt <- businessAddressRepo.findByBusinessId("business_id_1")
      //      _ <- IO(println(s"Query Result: $businessAddressOpt")) // Debug log the result
    } yield expect(businessAddressOpt == Some(expectedResult))
  }
}
