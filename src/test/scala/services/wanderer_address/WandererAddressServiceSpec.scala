package services.wanderer_address

import cats.effect.IO
import models.users.*
import models.wanderer.wanderer_address.errors.AddressNotFound
import models.wanderer.wanderer_address.service.WandererAddress
import repositories.user_profile.WandererAddressRepositoryAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererAddressServiceSpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], userId: String): WandererAddress =
    WandererAddress(
      id = Some(1),
      userId = userId,
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  class MockWandererAddressRepository(
                                       existingWandererAddress: Map[String, WandererAddress] = Map.empty
                                     ) extends WandererAddressRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, WandererAddress]] = IO.pure(existingWandererAddress)

    override def createUserAddress(user: WandererAddress): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def findByUserId(userId: String): IO[Option[WandererAddress]] = IO.pure(existingWandererAddress.get(userId))

    override def updateAddressDynamic(userId: String, street: Option[String], city: Option[String], country: Option[String], county: Option[String], postcode: Option[String]): IO[Option[WandererAddress]] = ???

    override def createRegistrationWandererAddress(userId: String): IO[Int] =
      IO.pure(1)
  }


  test(".getAddressDetailsByUserId() - when there is an existing user address details given a user_id should return the correct address details - Right(address)") {

    val existingAddressForUser = testAddress(Some(1), "user_id_1")

    val mockWandererAddressRepository = new MockWandererAddressRepository(Map("user_id_1" -> existingAddressForUser))
    val service = new WandererAddressServiceImpl[IO](mockWandererAddressRepository)

    for {
      result <- service.getAddressDetailsByUserId("user_id_1")
    } yield {
      expect(result == Right(existingAddressForUser))
    }
  }

  test(".getAddressDetailsByUserId() - when there are no existing user address details given a user_id should return Left(AddressNotFound)") {

    val existingAddressForUser = testAddress(Some(1), "user_id_1")

    val mockWandererAddressRepository = new MockWandererAddressRepository(Map())
    val service = new WandererAddressServiceImpl[IO](mockWandererAddressRepository)

    for {
      result <- service.getAddressDetailsByUserId("user_id_1")
    } yield {
      expect(result == Left(AddressNotFound))
    }
  }

  test(".created() - when given a WandererAddress successfully create the address") {

    val sampleAddress = testAddress(Some(1), "user_id_1")

    val mockWandererAddressRepository = new MockWandererAddressRepository(Map())
    val service = WandererAddressService(mockWandererAddressRepository)

    for {
      result <- service.createAddress(sampleAddress)
    } yield {
      expect(result == 1)
    }
  }
}
