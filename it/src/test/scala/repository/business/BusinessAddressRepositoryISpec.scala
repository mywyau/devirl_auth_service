package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import models.business.address.CreateBusinessAddressRequest
import models.business.address.BusinessAddressPartial
import models.database.DeleteSuccess
import models.desk.deskSpecifications.PrivateDesk
import repositories.business.BusinessAddressRepositoryImpl
import repository.fragments.business.BusinessAddressRepoFragments.*
import shared.TransactorResource
import testData.BusinessTestConstants.*
import testData.TestConstants.*
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag
import models.business.address.CreateBusinessAddressRequest
import models.business.address.BusinessAddressPartial

class BusinessAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessAddressTable.update.run.transact(transactor.xa).void *>
        resetBusinessAddressTable.update.run.transact(transactor.xa).void *>
        insertBusinessAddressData.update.run.transact(transactor.xa).void
    )

  def testBusinessAddressRequest(userId: String, businessId: String): CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("mikey_corp"),
      buildingName = Some("butter building"),
      floorNumber = Some("floor 1"),
      street = Some("Main street 123"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("fake county"),
      postcode = Some("123456"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

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
      BusinessAddressPartial(
        userId = "USER001",
        businessId = "BUS001",
        buildingName = Some("Innovation Tower"),
        floorNumber = Some("5"),
        street = Some("123 Tech Street"),
        city = Some("San Francisco"),
        country = Some("USA"),
        county = Some("California"),
        postcode = Some("94105"),
        latitude = Some(37.774929),
        longitude = Some(-122.419416)
      )

    for {
      businessAddressOpt <- businessAddressRepo.findByBusinessId("BUS001")
    } yield expect(businessAddressOpt == Some(expectedResult))
  }

  test(".deleteBusinessAddress() - should delete the business address if business_id exists for a previously existing business address") { businessAddressRepo =>

    val userId = "USER002"
    val businessId = "BUS002"

    val expectedResult =
      BusinessAddressPartial(
        userId = userId,
        businessId = businessId,
        buildingName = Some("Global Tower"),
        floorNumber = Some("12"),
        street = Some("456 Global Ave"),
        city = Some("New York"),
        country = Some("USA"),
        county = Some("New York"),
        postcode = Some("123456"),
        latitude = Some(40.712776),
        longitude = Some(-74.005974)
      )

    for {
      firstFindResult <- businessAddressRepo.findByBusinessId(businessId)
      deleteResult <- businessAddressRepo.delete(businessId)
      afterDeletionFindResult <- businessAddressRepo.findByBusinessId(businessId)
    } yield expect.all(
      firstFindResult == Some(expectedResult),
      deleteResult == Valid(DeleteSuccess),
      afterDeletionFindResult == None
    )
  }
}
