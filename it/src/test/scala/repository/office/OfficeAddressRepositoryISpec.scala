package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.database.*
import models.desk.deskSpecifications.PrivateDesk
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office_listing.requests.OfficeListingRequest
import repositories.office.OfficeAddressRepositoryImpl
import repository.constants.OfficeAddressRepoITConstants.*
import repository.fragments.OfficeAddressRepoFragments.*
import shared.TransactorResource
import testData.OfficeTestConstants.*
import testData.TestConstants.*
import weaver.GlobalRead
import weaver.IOSuite

class OfficeAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeAddressTable.update.run.transact(transactor.xa).void *>
        resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
        insertOfficeAddressTable.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, OfficeAddressRepositoryImpl[IO]] = {
    val setup =
      for {
        transactor <- global.getOrFailR[TransactorResource]()
        officeAddressRepo = new OfficeAddressRepositoryImpl[IO](transactor.xa)
        createSchemaIfNotPresent <- initializeSchema(transactor)
      } yield officeAddressRepo

    setup
  }

  test(".findByBusinessId() - should return the office address if business_id exists for a previously created office address") { officeAddressRepo =>

    val expectedResult = testOfficeAddressPartial("businessId1", "officeId1")

    for {
      officeAddressOpt <- officeAddressRepo.findByOfficeId("officeId1")
    } yield expect(officeAddressOpt == Valid(expectedResult))
  }

  test(".update() - should return UpdateSucess and update the office address if office_id exists for a previously created office address") { officeAddressRepo =>

    val businessId = businessId6
    val officeId = officeId6

    val createRequest = createInitialOfficeAddress(businessId, officeId)

    val request =
      UpdateOfficeAddressRequest(
        buildingName = Some("Empire State Building"),
        floorNumber = Some("5th Floor"),
        street = Some("Main street 123"),
        city = Some("New York"),
        country = Some("USA"),
        county = Some("Manhattan"),
        postcode = Some("123456"),
        latitude = Some(40.748817),
        longitude = Some(-73.985428)
      )

    for {
      officeAddressOpt <- officeAddressRepo.create(createRequest)
      officeAddressOpt <- officeAddressRepo.update(officeId, request)
    } yield expect(officeAddressOpt == Valid(UpdateSuccess))
  }

  test(".delete() - should delete the office address for office_id_2 if it exists for a previously created office address") { officeAddressRepo =>

    val officeId = officeId2

    for {
      result <- officeAddressRepo.delete(officeId)
    } yield expect(result == Valid(DeleteSuccess))
  }
}
