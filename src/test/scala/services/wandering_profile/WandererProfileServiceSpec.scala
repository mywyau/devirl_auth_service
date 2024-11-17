package services.wandering_profile

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.effect.IO
import cats.effect.kernel.Ref
import cats.implicits.*
import models.auth.RegisterPasswordErrors
import models.users.*
import models.users.adts.*
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
            val updated = details.copy(
              username = username.getOrElse(details.username),
              password_hash = passwordHash.getOrElse(details.password_hash),
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
              street = street.getOrElse(address.street),
              city = city.getOrElse(address.city),
              country = country.getOrElse(address.country),
              county = county.orElse(address.county),
              postcode = postcode.getOrElse(address.postcode)
            )
            (data + (userId -> updated), Some(updated))
          case None => (data, None)
        }
      }

    override def createUserAddress(user: WandererAddress): IO[Int] = ???
  }

  class MockWandererPersonalDetailsRepository(initialData: Map[String, WandererPersonalDetails]) extends WandererPersonalDetailsRepositoryAlgebra[IO] {
    private val dataRef: Ref[IO, Map[String, WandererPersonalDetails]] = Ref.unsafe(initialData)

    override def findByUserId(userId: String): IO[Option[WandererPersonalDetails]] =
      dataRef.get.map(_.get(userId))

    override def updatePersonalDetailsDynamic(
                                               userId: String,
                                               contactNumber: Option[String],
                                               firstName: Option[String],
                                               lastName: Option[String],
                                               email: Option[String],
                                               company: Option[String]
                                             ): IO[Option[WandererPersonalDetails]] =
      dataRef.modify { data =>
        data.get(userId) match {
          case Some(details) =>
            val updated = details.copy(
              contact_number = contactNumber.getOrElse(details.contact_number),
              first_name = firstName.getOrElse(details.first_name),
              last_name = lastName.getOrElse(details.last_name),
              email = email.getOrElse(details.email),
              company = company.getOrElse(details.company)
            )
            (data + (userId -> updated), Some(updated))
          case None => (data, None)
        }
      }

    override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): IO[Int] = ???
  }

  class MockPasswordService extends PasswordServiceAlgebra[IO] {

    override def hashPassword(password: String): IO[String] = IO.pure(password)

    override def checkPassword(password: String, hash: String): IO[Boolean] = IO.pure(true)

    override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] = ???
  }

  test("createProfile - should return a complete profile for an existing user") {
    val userId = "user_id_1"

    val mockLoginDetails = UserLoginDetails(
      id = Some(1),
      user_id = userId,
      username = "username",
      password_hash = "hashed_password",
      email = "user1@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val mockAddress = WandererAddress(
      id = Some(1),
      user_id = userId,
      street = "123 Main St",
      city = "Sample City",
      country = "Sample Country",
      county = Some("County"),
      postcode = "12345",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val mockPersonalDetails = WandererPersonalDetails(
      id = Some(1),
      user_id = userId,
      first_name = "John",
      last_name = "Doe",
      contact_number = "1234567890",
      email = "john.doe@example.com",
      company = "Sample Company",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
            user_id = userId,
            first_name = Some("John"),
            last_name = Some("Doe"),
            contact_number = Some("1234567890"),
            email = Some("john.doe@example.com"),
            company = Some("Sample Company"),
            created_at = mockPersonalDetails.created_at,
            updated_at = mockPersonalDetails.updated_at
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
            created_at = mockAddress.created_at,
            updated_at = mockAddress.updated_at
          )
        ),
        role = Some(Wanderer),
        created_at = mockLoginDetails.created_at,
        updated_at = mockLoginDetails.updated_at
      )
    ))
  }

  test("updateProfile - should dynamically update the user profile") {

    val userId = "user_id_1"

    val mockLoginDetails =
      UserLoginDetails(
        id = Some(1),
        user_id = userId,
        username = "old_username",
        password_hash = "old_hashed_password",
        email = "old_email@example.com",
        role = Wanderer,
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val mockAddress = WandererAddress(
      id = Some(1),
      user_id = userId,
      street = "123 Old St",
      city = "Old City",
      country = "Old Country",
      county = Some("Old County"),
      postcode = "OLD123",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val mockPersonalDetails = WandererPersonalDetails(
      id = Some(1),
      user_id = userId,
      first_name = "Old John",
      last_name = "Old Doe",
      contact_number = "0000000000",
      email = "old.john@example.com",
      company = "Old Corp",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
        contactNumber = Some("1234567890"),
        firstName = Some("New John"),
        lastName = Some("New Doe"),
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
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    val personalDetails =
      UserPersonalDetails(
        user_id = userId,
        first_name = Some("Old John"),
        last_name = Some("Old Doe"),
        contact_number = Some("0000000000"),
        email = Some("old.john@example.com"),
        company = Some("Old Corp"),
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
            password_hash = "new_hashed_password",
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
              first_name = Some("New John"),
              last_name = Some("New Doe"),
              contact_number = Some("1234567890"),
              email = Some("new.john@example.com"),
              company = Some("New Corp")
            )
          ),
        role = Some(Admin),
        created_at = mockLoginDetails.created_at,
        updated_at = mockLoginDetails.updated_at
      )
    ))
  }

}
