package repository

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.users.adts.Wanderer
import models.users.wanderer_address.service.WandererAddress
import models.users.wanderer_profile.profile.UserLoginDetails
import repositories.users.WandererAddressRepositoryImpl
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class WandererAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = WandererAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      sql"""
         CREATE TABLE IF NOT EXISTS wanderer_address (
            id BIGSERIAL PRIMARY KEY,
            user_id VARCHAR(255) NOT NULL,
            street VARCHAR(255) NOT NULL,
            city VARCHAR(255) NOT NULL,
            country VARCHAR(255) NOT NULL,
            county VARCHAR(255) NOT NULL,
            postcode VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
         );
      """.update.run.transact(transactor.xa).void *>
        sql"TRUNCATE TABLE wanderer_address RESTART IDENTITY"
          .update.run.transact(transactor.xa).void
    )

  def testWandererAddress(id: Option[Int], user_id: String) =
    WandererAddress(
      id = id,
      user_id = user_id,
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 3NJ",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  private def seedTestAddresses(wandererAddressRepo: WandererAddressRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testWandererAddress(Some(1), "user_id_1"),
      testWandererAddress(Some(2), "user_id_2"),
      testWandererAddress(Some(3), "user_id_3"),
      testWandererAddress(Some(4), "user_id_4"),
      testWandererAddress(Some(5), "user_id_5")
    )
    users.traverse(wandererAddressRepo.createUserAddress).void
  }

  def sharedResource: Resource[IO, WandererAddressRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      wandererAddressRepo = new WandererAddressRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
      seedTable <- Resource.eval(seedTestAddresses(wandererAddressRepo))
    } yield wandererAddressRepo

    setup
  }

//  // Test case to verify user creation and retrieval by username
//  test(".createUser() - should insert a new user") { wandererAddressRepo =>
//
//    val user = testWandererAddress(Some(4), "user_id_4")
//
//    for {
//      result <- wandererAddressRepo.createUserAddress(user)
//      userOpt <- wandererAddressRepo.findByUserId("user_id_4")
//    } yield expect(result == 1) and expect(userOpt.contains(user))
//  }

  test(".findByUserId() - should return the user if user_id exists") { wandererAddressRepo =>

    val userAddress = testWandererAddress(Some(1), "user_id_1")

    for {
      userAddressOpt <- wandererAddressRepo.findByUserId("user_id_1")
    } yield expect(userAddressOpt == Some(userAddress))
  }

  test(".updateAddressDynamic() - should update a user's address details") { wandererAddressRepo =>

    val wandererAddress = testWandererAddress(Some(5), "user_id_5")

    val updatedWandererAddress =
      Some(
        wandererAddress.copy(
          street = "New Street",
          city = "New City",
          country = "New Country",
          county = Some("New County"),
          postcode = "New postcode"
        )
      )

    for {
      updatedResult <- wandererAddressRepo.updateAddressDynamic(
        userId = "user_id_5",
        street = Some("New Street"),
        city = Some("New City"),
        country = Some("New Country"),
        county = Some("New County"),
        postcode = Some("New postcode")
      )
      wandererAddressOpt <- wandererAddressRepo.findByUserId("user_id_5")
    } yield expect(wandererAddressOpt == updatedWandererAddress)
  }
}
