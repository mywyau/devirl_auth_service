package repository

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repositories.user_profile.WandererPersonalDetailsRepositoryImpl
import repository.fragments.WandererPersonalDetailsRepositoryFragments.{createWandererPersonalDetailsTable, resetWandererPersonalDetailsTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class WandererPersonalDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = WandererPersonalDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createWandererPersonalDetailsTable.update.run.transact(transactor.xa).void *>
        resetWandererPersonalDetailsTable.update.run.transact(transactor.xa).void
    )

  def testWandererPersonalDetails(id: Option[Int], userId: String): WandererPersonalDetails =
    WandererPersonalDetails(
      id = id,
      userId = userId,
      firstName = Some("bob"),
      lastName = Some("smith"),
      contactNumber = Some("0123456789"),
      email = Some("user_1@gmail.com"),
      company = Some("apple"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  private def seedTestPersonalDetails(wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryImpl[IO]): IO[Unit] = {
    val personalDetails =
      List(
        testWandererPersonalDetails(Some(1), "user_id_1"),
        testWandererPersonalDetails(Some(2), "user_id_2"),
        testWandererPersonalDetails(Some(3), "user_id_3"),
        testWandererPersonalDetails(Some(4), "user_id_4"),
        testWandererPersonalDetails(Some(5), "user_id_5"),
      )
    personalDetails.traverse(wandererPersonalDetailsRepo.createPersonalDetails).void
  }

  def sharedResource: Resource[IO, WandererPersonalDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      wandererPersonalDetailsRepo = new WandererPersonalDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestPersonalDetails(wandererPersonalDetailsRepo))
      seedTableOnlyUserId <- Resource.eval(wandererPersonalDetailsRepo.createRegistrationPersonalDetails("user_id_6"))
    } yield wandererPersonalDetailsRepo

    setup
  }

  test(".findByUserId() - should return the user's personal details if user_id exists") { wandererPersonalDetailsRepo =>

    val personalDetails = testWandererPersonalDetails(Some(1), "user_id_1")

    for {
      personalDetailsOpt <- wandererPersonalDetailsRepo.findByUserId("user_id_1")
    } yield expect(personalDetailsOpt == Some(personalDetails))
  }

  // TODO: Possibly remove may not need
  //  // Test case to verify user creation and retrieval by username
  //  test(".createPersonalDetails() - should insert a new user's personal details") { wandererPersonalDetailsRepo =>
  //
  //    val personalDetails = testWandererPersonalDetails(Some(4), "user_id_4")
  //
  //    for {
  //      result <- wandererPersonalDetailsRepo.createPersonalDetails(personalDetails)
  //      userOpt <- wandererPersonalDetailsRepo.findByUserId("user_id_4")
  //    } yield expect(result == 1) and expect(userOpt.contains(personalDetails))
  //  }

  // Test case to verify user creation and retrieval by username

  test(".updatePersonalDetailsDynamic() - should insert a new user's personal details") { wandererPersonalDetailsRepo =>

    val personalDetails = testWandererPersonalDetails(Some(5), "user_id_5")

    val updatedPersonalDetails =
      Some(
        personalDetails.copy(
          firstName = Some("Michael"),
          lastName = Some("Yau"),
          contactNumber = Some("07402205071"),
          email = Some("mike@gmail.com"),
          company = Some("capgemini")
        )
      )
    for {
      updatedResult <- wandererPersonalDetailsRepo.updatePersonalDetailsDynamic(
        userId = "user_id_5",
        firstName = Some("Michael"),
        lastName = Some("Yau"),
        contactNumber = Some("07402205071"),
        email = Some("mike@gmail.com"),
        company = Some("capgemini"),
      )
      userOpt <- wandererPersonalDetailsRepo.findByUserId("user_id_5")
    } yield expect(userOpt == updatedPersonalDetails)
  }
}
