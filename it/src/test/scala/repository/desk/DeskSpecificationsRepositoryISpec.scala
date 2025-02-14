package repository

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.database.CreateSuccess
import models.database.DeleteSuccess
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import repositories.desk.DeskSpecificationsRepositoryImpl
import repository.fragments.desk.DeskSpecificationsRepoFragments.*
import shared.TransactorResource
import testData.DeskTestConstants.*
import testData.TestConstants.*
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class DeskSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite with RepositoryISpecBase {

  type Res = DeskSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        resetDeskSpecificationsTable.update.run.transact(transactor.xa).void *>
        insertDeskSpecifications.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, DeskSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessDeskRepo = new DeskSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessDeskRepo

    setup
  }

  test(".findByDeskId() - should return the desk listing details for a given deskId") { businessDeskRepo =>

    val expectedResult =
      DeskSpecificationsPartial(
        deskId = deskId1,
        deskName = deskName1,
        description = Some(description1),
        deskType = Some(PrivateDesk),
        quantity = Some(5),
        features = Some(List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp")),
        openingHours = Some(deskOpeningHours),
        rules = Some(rules)
      )

    for {
      deskSpecificationsOpt <- businessDeskRepo.findByDeskId(deskId1)
    } yield expect(deskSpecificationsOpt == Some(expectedResult))
  }

  test(".findByOfficeId() - should return the all desk listing for a given officeId") { businessDeskRepo =>

    val expectedResult =
      DeskSpecificationsPartial(
        deskId = deskId1,
        deskName = deskName1,
        description = Some(description1),
        deskType = Some(PrivateDesk),
        quantity = Some(5),
        features = Some(List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp")),
        openingHours = Some(deskOpeningHours),
        rules = Some(rules)
      )

    for {
      deskSpecificationsOpt <- businessDeskRepo.findByOfficeId(officeId1)
    } yield expect(deskSpecificationsOpt == List(expectedResult))
  }

  test(".create() - should return CreateSuccess for successfuly creating a desk listing") { businessDeskRepo =>

    val createRequest = sampleUpdateDeskSpecificationsRequest

    for {
      deskSpecificationsOpt <- businessDeskRepo.create(createRequest)
    } yield expect(deskSpecificationsOpt == Valid(CreateSuccess))
  }

  test(".delete() - should return DeleteSuccess for successfuly deleting the desk based on the deskId") { businessDeskRepo =>
    for {
      deskSpecificationsOpt <- businessDeskRepo.delete(deskId2)
    } yield expect(deskSpecificationsOpt == Valid(DeleteSuccess))
  }
}
