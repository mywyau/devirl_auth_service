package repository.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.office.address_details.OfficeAddress
import models.office.specifications.OfficeAvailability
import models.office.office_listing.requests.OfficeListingRequest
import repositories.office.OfficeAddressRepositoryImpl
import repository.fragments.OfficeAddressRepoFragments.{createOfficeAddressTable, resetOfficeAddressTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class OfficeAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeAddressTable.update.run.transact(transactor.xa).void *>
        resetOfficeAddressTable.update.run.transact(transactor.xa).void
    )

  def testOfficeAddress(id: Option[Int], businessId: String, officeId: String): OfficeAddress = {
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = officeId,
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

  private def seedTestAddresses(officeAddressRepo: OfficeAddressRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testOfficeAddress(Some(1), "business_id_1", "office_1"),
      testOfficeAddress(Some(2), "business_id_2", "office_2"),
      testOfficeAddress(Some(3), "business_id_3", "office_3"),
      testOfficeAddress(Some(4), "business_id_4", "office_4"),
      testOfficeAddress(Some(5), "business_id_5", "office_5")
    )
    users.traverse(officeAddressRepo.createOfficeAddress).void
  }

  def sharedResource: Resource[IO, OfficeAddressRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeAddressRepo = new OfficeAddressRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestAddresses(officeAddressRepo))
    } yield officeAddressRepo

    setup
  }

  test(".findByBusinessId() - should return the office address if business_id exists for a previously created office address") { officeAddressRepo =>

    val officeAddress = testOfficeAddress(Some(1), "business_id_1", "office_1")

    val expectedResult =
      OfficeAddress(
        id = Some(1),
        businessId = "business_id_1",
        officeId = "office_1",
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
      officeAddressOpt <- officeAddressRepo.findByBusinessId("business_id_1")
      //      _ <- IO(println(s"Query Result: $officeAddressOpt")) // Debug log the result
    } yield expect(officeAddressOpt == Some(expectedResult))
  }
}
