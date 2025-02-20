package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import models.desk.deskSpecifications.PrivateDesk
import models.database.DeleteSuccess
import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import repositories.office.OfficeContactDetailsRepositoryImpl
import repository.fragments.OfficeContactDetailsRepoFragments.createOfficeContactDetailsTable
import repository.fragments.OfficeContactDetailsRepoFragments.insertOfficeContactDetailsData
import repository.fragments.OfficeContactDetailsRepoFragments.resetOfficeContactDetailsTable
import repository.fragments.OfficeContactDetailsRepoFragments.sameBusinessIdContactDetailsData
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class DeleteAllOfficeContactDetailsRepoISpec(global: GlobalRead) extends IOSuite {
  type Res = OfficeContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
        sameBusinessIdContactDetailsData.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, OfficeContactDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeContactDetailsRepo = new OfficeContactDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield officeContactDetailsRepo

    setup
  }

  test(".deleteAllByBusinessId() - should delete multiple office contact details for the same business_id - i.e. BUS12345") { officeContactDetailsRepo =>
    for {
      result <- officeContactDetailsRepo.deleteAllByBusinessId("BUS12345")
    } yield expect(result == Valid(DeleteSuccess))
  }
}
