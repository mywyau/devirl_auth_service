package repository

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repositories.users.WandererPersonalDetailsRepositoryImpl
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class WandererPersonalDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = WandererPersonalDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      sql"""
         CREATE TABLE IF NOT EXISTS wanderer_personal_details (
            id BIGSERIAL PRIMARY KEY,
            user_id VARCHAR(255) NOT NULL,
            first_name VARCHAR(255) NOT NULL,
            last_name VARCHAR(255) NOT NULL,
            contact_number VARCHAR(100) NOT NULL,
            email VARCHAR(255) NOT NULL,
            company VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
         );
      """.update.run.transact(transactor.xa).void *>
        sql"TRUNCATE TABLE wanderer_personal_details RESTART IDENTITY"
          .update.run.transact(transactor.xa).void
    )

  def testWandererPersonalDetails(id: Option[Int], user_id: String): WandererPersonalDetails =
    WandererPersonalDetails(
      id = id,
      user_id = user_id,
      first_name = "bob",
      last_name = "smith",
      contact_number = "0123456789",
      email = "user_1@gmail.com",
      company = "apple",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  private def seedTestPersonalDetails(wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryImpl[IO]): IO[Unit] = {
    val personalDetails =
      List(
        testWandererPersonalDetails(Some(1), "user_id_1"),
        testWandererPersonalDetails(Some(2), "user_id_2"),
        testWandererPersonalDetails(Some(3), "user_id_3")
      )
    personalDetails.traverse(wandererPersonalDetailsRepo.createPersonalDetails).void
  }

  def sharedResource: Resource[IO, WandererPersonalDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      wandererPersonalDetailsRepo = new WandererPersonalDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestPersonalDetails(wandererPersonalDetailsRepo))
    } yield wandererPersonalDetailsRepo

    setup
  }

  test(".findByUserId() - should return the user's personal details if user_id exists") { wandererPersonalDetailsRepo =>

    val personalDetails = testWandererPersonalDetails(Some(1), "user_id_1")

    for {
      personalDetailsOpt <- wandererPersonalDetailsRepo.findByUserId("user_id_1")
    } yield expect(personalDetailsOpt == Some(personalDetails))
  }

  // Test case to verify user creation and retrieval by username
  test(".createPersonalDetails() - should insert a new user's personal details") { wandererPersonalDetailsRepo =>

    val personalDetails = testWandererPersonalDetails(Some(4), "user_id_4")

    for {
      result <- wandererPersonalDetailsRepo.createPersonalDetails(personalDetails)
      userOpt <- wandererPersonalDetailsRepo.findByUserId("user_id_4")
    } yield expect(result == 1) and expect(userOpt.contains(personalDetails))
  }
}
