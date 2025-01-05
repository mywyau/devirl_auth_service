package repository.office

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.office.adts.OpenPlanOffice
import models.office.adts.PrivateOffice
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import repositories.office.OfficeSpecificationsRepositoryImpl
import repository.fragments.OfficeSpecificationsRepoFragments.createOfficeSpecsTable
import repository.fragments.OfficeSpecificationsRepoFragments.insertOfficeSpecificationData
import repository.fragments.OfficeSpecificationsRepoFragments.resetOfficeSpecsTable
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class OfficeSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        resetOfficeSpecsTable.update.run.transact(transactor.xa).void *>
        insertOfficeSpecificationData.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, OfficeSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      officeSpecsRepo = new OfficeSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield officeSpecsRepo

    setup
  }

  test(
    ".findByOfficeId() - should return the office specifications if office_id exists for a previously created office specifications"
  ) { officeSpecificationsRepo =>

    val expectedResult =
      OfficeSpecificationsPartial(
        businessId = "BUS001",
        officeId = "OFF001",
        officeName = Some("Main Office"),
        description = Some("Spacious and well-lit office for teams."),
        officeType = Some(PrivateOffice),
        numberOfFloors = Some(2),
        totalDesks = Some(20),
        capacity = Some(50),
        amenities = Some(List("WiFi", "Parking")),
        availability = Some(
          OfficeAvailability(
            days = List("Monday", "Friday"),
            startTime = LocalTime.of(9, 0, 0),
            endTime = LocalTime.of(17, 0, 0)
          )
        ),
        rules = Some("No smoking indoors.")
      )

    for {
      officeSpecsOpt <- officeSpecificationsRepo.findByOfficeId("OFF001")
    } yield expect(officeSpecsOpt == Some(expectedResult))
  }
}
