package repository.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.office_listing.requests.OfficeListingRequest
import models.office.specifications.OfficeAvailability
import repositories.office.OfficeAddressRepositoryImpl
import repository.constants.OfficeAddressRepoITConstants.*
import repository.fragments.OfficeAddressRepoFragments.{createOfficeAddressTable, insertOfficeAddressTable, resetOfficeAddressTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite}

import java.time.LocalDateTime

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

    val expectedResult = testOfficeAddress(Some(1), "business_id_1", "office_id_1")

    for {
      officeAddressOpt <- officeAddressRepo.findByOfficeId("office_id_1")
      //      _ <- IO(println(s"Query Result: $officeAddressOpt")) // Debug log the result
    } yield expect(officeAddressOpt == Some(expectedResult))
  }
}
