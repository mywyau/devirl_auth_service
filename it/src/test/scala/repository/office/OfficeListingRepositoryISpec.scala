package repository.office

import cats.data.Validated.Valid
import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.office.office_listing.requests.InitiateOfficeListingRequest
import repositories.office.OfficeListingRepositoryImpl
import repository.constants.OfficeListingRepoITConstants.*
import repository.fragments.OfficeAddressRepoFragments.*
import repository.fragments.OfficeContactDetailsRepoFragments.*
import repository.fragments.OfficeSpecificationsRepoFragments.*
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite}

import java.time.LocalDateTime

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

    val expectedResult = testOfficeListing(Some(1), "business_id_6", "office_id_6")

    for {
      officeAddressOpt <- officeAddressRepo.findByOfficeId("office_id_6")
    } yield expect(officeAddressOpt == Some(expectedResult))
  }

  test(".initiate() - should return the office listing if business_id exists for a previously created office listing") { officeListingRepo =>

    val request = InitiateOfficeListingRequest("business_id_2", "office_id_2")

    for {
      officeListingOpt <- officeListingRepo.initiate(request)
    } yield expect(officeListingOpt == Valid(3))
  }
}
