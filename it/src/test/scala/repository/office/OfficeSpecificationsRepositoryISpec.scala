package repository.office

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import controllers.constants.OfficeSpecificationsControllerITConstants.testCreateOfficeSpecificationsRequest
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.database.CreateSuccess
import models.database.DeleteSuccess
import models.database.NotFoundError
import models.database.UpdateSuccess
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.adts.OpenPlanOffice
import models.office.adts.PrivateOffice
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import repositories.office.OfficeSpecificationsRepositoryImpl
import repository.constants.OfficeAddressRepoITConstants.createInitialOfficeAddress
import repository.fragments.OfficeSpecificationsRepoFragments.createOfficeSpecsTable
import repository.fragments.OfficeSpecificationsRepoFragments.insertOfficeSpecificationData
import repository.fragments.OfficeSpecificationsRepoFragments.resetOfficeSpecsTable
import shared.TransactorResource
import testData.OfficeTestConstants.*
import testData.TestConstants.*
import utils.Diffable
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
        businessId = businessId1,
        officeId = officeId1,
        officeName = Some(officeName1),
        description = Some(officeDescription1),
        officeType = Some(PrivateOffice),
        numberOfFloors = Some(threeFloors),
        totalDesks = Some(threeDesks),
        capacity = Some(capacityOfFifty),
        amenities = Some(List("WiFi", "Parking")),
        openingHours = Some(officeOpeningHours1),
        rules = Some("No smoking indoors.")
      )

    for {
      officeSpecsOpt <- officeSpecificationsRepo.findByOfficeId(officeId1)
      _ = officeSpecsOpt.foreach(office => Diffable.logDifferences(expectedResult, office)) // ✅ DRY Logging
    } yield expect(officeSpecsOpt.contains(expectedResult))
  }

  test(".update() - should update the office specification if office_id exists for a previously created office specification") { officeSpecificationsRepo =>

    val businessId = businessId6
    val officeId = officeId6

    val createRequest = testCreateOfficeSpecificationsRequest(businessId, officeId)

    val request =
      UpdateOfficeSpecificationsRequest(
        officeName = "Mikey Workspace",
        description = "A modern Mikey space located in the heart of downtown.",
        officeType = PrivateOffice,
        numberOfFloors = 100,
        totalDesks = 5000,
        capacity = 100000,
        amenities = List("Wi-Fi", "Coffee Machine", "Meeting Rooms"),
        openingHours = officeOpeningHours1,
        rules = Some("No loud conversations. Keep the desks clean.")
      )

    val expectedResult =
      OfficeSpecificationsPartial(
        businessId = businessId,
        officeId = officeId,
        officeName = Some("Mikey Workspace"),
        description = Some("A modern Mikey space located in the heart of downtown."),
        officeType = Some(PrivateOffice),
        numberOfFloors = Some(100),
        totalDesks = Some(5000),
        capacity = Some(100000),
        amenities = Some(List("Wi-Fi", "Coffee Machine", "Meeting Rooms")),
        openingHours = Some(officeOpeningHours1),
        rules = Some("No loud conversations. Keep the desks clean.")
      )

    for {
      createResult <- officeSpecificationsRepo.create(createRequest)
      updateResult <- officeSpecificationsRepo.update(officeId, request)
      findResult <- officeSpecificationsRepo.findByOfficeId(officeId)
    } yield expect.all(
      createResult == Valid(CreateSuccess),
      updateResult == Valid(UpdateSuccess),
      findResult == Some(expectedResult)
    )
  }

  test(".delete() - should delete the office specification if office_id exists for a previously created office specification - officeId2") { officeSpecificationsRepo =>
    for {
      deleteResult <- officeSpecificationsRepo.delete(officeId2)
    } yield expect(deleteResult == Valid(DeleteSuccess))
  }

  test(".delete() - should return a NotFoundError if office_id does not exist - SomeRandomId") { officeSpecificationsRepo =>
    for {
      deleteResult <- officeSpecificationsRepo.delete("SomeRandomId")
    } yield expect(deleteResult == Invalid(NonEmptyList.of(NotFoundError)))
  }

}
