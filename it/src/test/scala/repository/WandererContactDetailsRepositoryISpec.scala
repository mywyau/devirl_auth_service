package repository

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.users.wanderer_personal_details.service.WandererContactDetails
import repositories.users.WandererContactDetailsRepositoryImpl
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class WandererContactDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = WandererContactDetailsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      sql"""
         CREATE TABLE IF NOT EXISTS wanderer_contact_details (
            id BIGSERIAL PRIMARY KEY,
            user_id VARCHAR(255) NOT NULL,
            contact_number VARCHAR(100) NOT NULL,
            email VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
         );
      """.update.run.transact(transactor.xa).void *>
        sql"TRUNCATE TABLE wanderer_contact_details RESTART IDENTITY"
          .update.run.transact(transactor.xa).void
    )

  def testWandererContactDetails(id: Option[Int], user_id: String): WandererContactDetails =
    WandererContactDetails(
      id = id,
      user_id = user_id,
      contact_number = "0123456789",
      email = "user_1@gmail.com",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  private def seedTestContactDetails(wandererContactDetailsRepo: WandererContactDetailsRepositoryImpl[IO]): IO[Unit] = {
    val contactDetails =
      List(
        testWandererContactDetails(Some(1), "user_id_1"),
        testWandererContactDetails(Some(2), "user_id_2"),
        testWandererContactDetails(Some(3), "user_id_3")
      )
    contactDetails.traverse(wandererContactDetailsRepo.createContactDetails).void
  }

  def sharedResource: Resource[IO, WandererContactDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      wandererContactDetailsRepo = new WandererContactDetailsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestContactDetails(wandererContactDetailsRepo))
    } yield wandererContactDetailsRepo

    setup
  }

  test(".findByUserId() - should return the user's contact details if user_id exists") { wandererContactDetailsRepo =>

    val contactDetails = testWandererContactDetails(Some(1), "user_id_1")

    for {
      contactDetailsOpt <- wandererContactDetailsRepo.findByUserId("user_id_1")
    } yield expect(contactDetailsOpt == Some(contactDetails))
  }

  // Test case to verify user creation and retrieval by username
  test(".createContactDetails() - should insert a new user's contact details") { wandererContactDetailsRepo =>

    val contactDetails = testWandererContactDetails(Some(4), "user_id_4")

    for {
      result <- wandererContactDetailsRepo.createContactDetails(contactDetails)
      userOpt <- wandererContactDetailsRepo.findByUserId("user_id_4")
    } yield expect(result == 1) and expect(userOpt.contains(contactDetails))
  }
}
