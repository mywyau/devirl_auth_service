package services.wandering_profile

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.effect.IO
import cats.effect.kernel.Ref
import cats.implicits.*
import models.users.*
import models.users.adts.*
import models.users.registration.RegisterPasswordErrors
import models.users.wanderer_address.service.WandererAddress
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}
import models.users.wanderer_profile.requests.{UpdateAddress, UpdateLoginDetails, UpdatePersonalDetails}
import repositories.users.{UserLoginDetailsRepositoryAlgebra, WandererAddressRepositoryAlgebra, WandererPersonalDetailsRepositoryAlgebra}
import services.password.PasswordServiceAlgebra
import services.wanderer_profile.WandererProfileServiceImpl
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererProfileServiceSpec extends SimpleIOSuite {

  class MockUserLoginDetailsRepository(initialData: Map[String, UserLoginDetails]) extends UserLoginDetailsRepositoryAlgebra[IO] {
    private val dataRef: Ref[IO, Map[String, UserLoginDetails]] = Ref.unsafe(initialData)

    override def findByUserId(userId: String): IO[Option[UserLoginDetails]] =
      dataRef.get.map(_.get(userId))

    override def updateUserLoginDetailsDynamic(
                                                userId: String,
                                                username: Option[String],
                                                passwordHash: Option[String],
                                                email: Option[String],
                                                role: Option[Role]
                                              ): IO[Option[UserLoginDetails]] =
      dataRef.modify { data =>
        data.get(userId) match {
          case Some(details) =>
            val updated =
              details.copy(
                username = username.getOrElse(details.username),
                passwordHash = passwordHash.getOrElse(details.passwordHash),
                email = email.getOrElse(details.email),
                role = role.getOrElse(details.role)
              )
            (data + (userId -> updated), Some(updated))
          case None => (data, None)
        }
      }

    override def createUserLoginDetails(user: UserLoginDetails): IO[Int] = ???

    override def findByUsername(username: String): IO[Option[UserLoginDetails]] = ???

    override def findByEmail(email: String): IO[Option[UserLoginDetails]] = ???

    override def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): IO[Option[UserLoginDetails]] = ???
  }

  class MockWandererAddressRepository(initialData: Map[String, WandererAddress]) extends WandererAddressRepositoryAlgebra[IO] {
    private val dataRef: Ref[IO, Map[String, WandererAddress]] = Ref.unsafe(initialData)

    override def findByUserId(userId: String): IO[Option[WandererAddress]] =
      dataRef.get.map(_.get(userId))

    override def updateAddressDynamic(
                                       userId: String,
                                       street: Option[String],
                                       city: Option[String],
                                       country: Option[String],
                                       county: Option[String],
                                       postcode: Option[String]
                                     ): IO[Option[WandererAddress]] =
      dataRef.modify { data =>
        data.get(userId) match {
          case Some(address) =>
            val updated = address.copy(
              street = street,
              city = city,
              country = country,
              county = county,
              postcode = postcode
            )
            (data + (userId -> updated), Some(updated))
          case None => (data, None)
        }
      }

    override def createUserAddress(user: WandererAddress): IO[Int] =
      IO.pure(1)

    override def createRegistrationWandererAddress(userId: String): IO[Int] =
      IO.pure(1)
  }

  class MockWandererPersonalDetailsRepository(initialData: Map[String, WandererPersonalDetails])
    extends WandererPersonalDetailsRepositoryAlgebra[IO] {

    private val dataRef: Ref[IO, Map[String, WandererPersonalDetails]] = Ref.unsafe(initialData)

    override def findByUserId(userId: String): IO[Option[WandererPersonalDetails]] =
      dataRef.get.map(_.get(userId))

    override def updatePersonalDetailsDynamic(
                                               userId: String,
                                               firstName: Option[String],
                                               lastName: Option[String],
                                               contactNumber: Option[String],
                                               email: Option[String],
                                               company: Option[String]
                                             ): IO[Option[WandererPersonalDetails]] =
      dataRef.modify { data =>
        data.get(userId) match {
          case Some(details) =>
            val updated = details.copy(
              firstName = firstName.orElse(details.firstName),
              lastName = lastName.orElse(details.lastName),
              contactNumber = contactNumber.orElse(details.contactNumber),
              email = email.orElse(details.email),
              company = company.orElse(details.company)
            )
            println(s"Updating userId=$userId: $details -> $updated")
            (data + (userId -> updated), Some(updated))
          case None =>
            println(s"No details found for userId=$userId")
            (data, None)
        }
      }

    override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): IO[Int] = IO.pure(1)

    override def createRegistrationPersonalDetails(userId: String): IO[Int] = IO.pure(1)
  }


  class MockPasswordService extends PasswordServiceAlgebra[IO] {

    override def hashPassword(password: String): IO[String] = IO.pure(password)

    override def checkPassword(password: String, hash: String): IO[Boolean] = IO.pure(true)

    override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] = ???
  }

  test("createProfile - should return a complete profile for an existing user") {
    val userId = "user_id_1"

    val mockLoginDetails =
      UserLoginDetails(
        id = Some(1),
        userId = userId,
        username = "username",
        passwordHash = "hashed_password",
        email = "user1@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val mockAddress = WandererAddress(
      id = Some(1),
      userId = userId,
      street = Some("123 Main St"),
      city = Some("Sample City"),
      country = Some("Sample Country"),
      county = Some("County"),
      postcode = Some("12345"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val mockPersonalDetails = WandererPersonalDetails(
      id = Some(1),
      userId = userId,
      firstName = Some("John"),
      lastName = Some("Doe"),
      contactNumber = Some("1234567890"),
      email = Some("john.doe@example.com"),
      company = Some("Sample Company"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val mockLoginRepo = new MockUserLoginDetailsRepository(Map(userId -> mockLoginDetails))
    val mockAddressRepo = new MockWandererAddressRepository(Map(userId -> mockAddress))
    val mockPersonalRepo = new MockWandererPersonalDetailsRepository(Map(userId -> mockPersonalDetails))
    val mockPasswordService = new MockPasswordService

    val service = new WandererProfileServiceImpl[IO](mockLoginRepo, mockAddressRepo, mockPersonalRepo, mockPasswordService)

    for {
      profile <- service.createProfile(userId)
    } yield expect(profile == Valid(
      WandererUserProfile(
        userId = userId,
        userLoginDetails = Some(mockLoginDetails),
        userPersonalDetails = Some(
          UserPersonalDetails(
            userId = userId,
            firstName = Some("John"),
            lastName = Some("Doe"),
            contactNumber = Some("1234567890"),
            email = Some("john.doe@example.com"),
            company = Some("Sample Company"),
            createdAt = mockPersonalDetails.createdAt,
            updatedAt = mockPersonalDetails.updatedAt
          )
        ),
        userAddress = Some(
          UserAddress(
            userId = userId,
            street = Some("123 Main St"),
            city = Some("Sample City"),
            country = Some("Sample Country"),
            county = Some("County"),
            postcode = Some("12345"),
            createdAt = mockAddress.createdAt,
            updatedAt = mockAddress.updatedAt
          )
        ),
        role = Some(Wanderer),
        createdAt = mockLoginDetails.createdAt,
        updatedAt = mockLoginDetails.updatedAt
      )
    ))
  }

  test("updateProfile - should dynamically update the user profile") {

    val userId = "user_id_1"

    val mockLoginDetails =
      UserLoginDetails(
        id = Some(1),
        userId = userId,
        username = "old_username",
        passwordHash = "old_hashed_password",
        email = "old_email@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val mockAddress = WandererAddress(
      id = Some(1),
      userId = userId,
      street = Some("123 Old St"),
      city = Some("Old City"),
      country = Some("Old Country"),
      county = Some("Old County"),
      postcode = Some("OLD123"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val mockPersonalDetails =
      WandererPersonalDetails(
        id = Some(1),
        userId = userId,
        firstName = Some("Old John"),
        lastName = Some("Old Doe"),
        contactNumber = Some("0000000000"),
        email = Some("old.john@example.com"),
        company = Some("Old Corp"),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )


    val mockLoginRepo = new MockUserLoginDetailsRepository(Map(userId -> mockLoginDetails))
    val mockAddressRepo = new MockWandererAddressRepository(Map(userId -> mockAddress))
    val mockPersonalRepo = new MockWandererPersonalDetailsRepository(Map(userId -> mockPersonalDetails))
    val mockPasswordService = new MockPasswordService

    val service = new WandererProfileServiceImpl[IO](mockLoginRepo, mockAddressRepo, mockPersonalRepo, mockPasswordService)

    val updateLoginDetails =
      UpdateLoginDetails(
        username = Some("new_username"),
        passwordHash = Some("new_hashed_password"),
        email = Some("new_email@example.com"),
        role = Some(Admin)
      )

    val updateAddress =
      UpdateAddress(
        street = Some("456 New St"),
        city = Some("New City"),
        country = Some("New Country"),
        county = Some("New County"),
        postcode = Some("NEW123")
      )

    val updatePersonalDetails =
      UpdatePersonalDetails(
        firstName = Some("New John"),
        lastName = Some("New Doe"),
        contactNumber = Some("1234567890"),
        email = Some("new.john@example.com"),
        company = Some("New Corp")
      )

    val userAddress =
      UserAddress(
        userId = userId,
        street = Some("123 Old St"),
        city = Some("Old City"),
        country = Some("Old Country"),
        county = Some("Old County"),
        postcode = Some("OLD123"),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val personalDetails =
      UserPersonalDetails(
        userId = userId,
        firstName = Some("Old John"),
        lastName = Some("Old Doe"),
        contactNumber = Some("0000000000"),
        email = Some("old.john@example.com"),
        company = Some("Old Corp"),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )


    for {
      updatedProfile <- service.updateProfile(
        userId = userId,
        loginDetailsUpdate = Some(updateLoginDetails),
        addressUpdate = Some(updateAddress),
        personalDetailsUpdate = Some(updatePersonalDetails)
      )
    } yield expect(updatedProfile.contains(
      WandererUserProfile(
        userId = userId,
        userLoginDetails = Some(
          mockLoginDetails.copy(
            username = "new_username",
            passwordHash = "new_hashed_password",
            email = "new_email@example.com",
            role = Admin
          )
        ),
        userAddress =
          Some(
            userAddress.copy(
              street = Some("456 New St"),
              city = Some("New City"),
              country = Some("New Country"),
              county = Some("New County"),
              postcode = Some("NEW123")
            )
          ),
        userPersonalDetails =
          Some(
            personalDetails.copy(
              firstName = Some("New John"),
              lastName = Some("New Doe"),
              contactNumber = Some("1234567890"),
              email = Some("new.john@example.com"),
              company = Some("New Corp")
            )
          ),
        role = Some(Admin),
        createdAt = mockLoginDetails.createdAt,
        updatedAt = mockLoginDetails.updatedAt
      )
    ))
  }

}
