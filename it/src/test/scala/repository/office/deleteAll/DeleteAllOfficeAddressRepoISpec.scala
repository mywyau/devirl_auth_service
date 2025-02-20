package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.desk.deskSpecifications.PrivateDesk
import models.database.DeleteSuccess
import models.database.UpdateSuccess
import models.office.address_details.OfficeAddress
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.UpdateOfficeAddressRequest
import models.office_listing.requests.OfficeListingRequest

import repositories.office.OfficeAddressRepositoryImpl
import repository.constants.OfficeAddressRepoITConstants.*
import repository.fragments.OfficeAddressRepoFragments.createOfficeAddressTable
import repository.fragments.OfficeAddressRepoFragments.insertOfficeAddressTable
import repository.fragments.OfficeAddressRepoFragments.resetOfficeAddressTable
import repository.fragments.OfficeAddressRepoFragments.sameBusinessIdData
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite

import java.time.LocalDateTime

class DeleteAllOfficeAddressRepoISpec(global: GlobalRead) extends IOSuite {
  
  type Res = OfficeAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeAddressTable.update.run.transact(transactor.xa).void *>
        resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
        sameBusinessIdData.update.run.transact(transactor.xa).void
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

  test(".deleteAllByBusinessId() - should delete multiple offices for the same business_id - i.e. businessId1") { officeAddressRepo =>

    val expectedResult = testOfficeAddressPartial("businessId1", "officeId1")

    for {
      result <- officeAddressRepo.deleteAllByBusinessId("businessId1")
    } yield expect(result == Valid(DeleteSuccess))
  }
}
