package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import models.database.CreateSuccess
import models.database.DeleteSuccess
import models.office_listing.requests.InitiateOfficeListingRequest
import repositories.office.OfficeListingRepositoryImpl
import repository.constants.OfficeListingRepoITConstants.*
import repository.fragments.OfficeAddressRepoFragments.*
import repository.fragments.OfficeContactDetailsRepoFragments.*
import repository.fragments.OfficeSpecificationsRepoFragments.*
import shared.TransactorResource
import testData.OfficeTestConstants.*
import testData.TestConstants.*
import utils.Diffable
import weaver.GlobalRead
import weaver.IOSuite

class OfficeListingRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeListingRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeAddressTable.update.run.transact(transactor.xa).void *>
        resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
        initiateOfficeAddressData.update.run.transact(transactor.xa).void *>
        createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        initiateOfficeContactDetailsData.update.run.transact(transactor.xa).void *>
        createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        resetOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        initiateOfficeSpecificationData.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, OfficeListingRepositoryImpl[IO]] = {
    val setup =
      for {
        transactor <- global.getOrFailR[TransactorResource]()
        officeListingRepo = new OfficeListingRepositoryImpl[IO](transactor.xa)
        createSchemaIfNotPresent <- initializeSchema(transactor)
      } yield officeListingRepo

    setup
  }

  test(".findByOfficeId() - should return the office listing if office_id exists for a previously created office listing") { officeAddressRepo =>

    val expectedResult = testOfficeListing(businessId6, officeId6)

    for {
      officeAddressOpt <- officeAddressRepo.findByOfficeId(officeId6)
      _ = officeAddressOpt.foreach(officeSpec => Diffable.logDifferences(expectedResult, officeSpec))
    } yield expect(officeAddressOpt == Some(expectedResult))
  }

  test(".initiate() - should return the office listing if business_id exists for a previously created office listing") { officeListingRepo =>

    val request = InitiateOfficeListingRequest(businessId2, officeId2, "Office Name", "some desc")

    for {
      officeListingOpt <- officeListingRepo.initiate(request)
    } yield expect(officeListingOpt == Valid(CreateSuccess))
  }

  test(".delete() - should delete the office listing if office_id exists for a previously created office listing") { officeListingRepo =>

    val businessId = businessId6

    val createRequest = InitiateOfficeListingRequest(businessId6, businessId6, "Office 6", "some desc 6")

    for {
      createResult <- officeListingRepo.initiate(createRequest)
      deleteResult <- officeListingRepo.delete(businessId)
    } yield expect(deleteResult == Valid(DeleteSuccess))
  }
}
