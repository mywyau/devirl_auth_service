package services.registration.mocks

import cats.data.Validated
import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.adts.Role
import models.users.registration.RegisterPasswordErrors
import models.users.wanderer_address.service.WandererAddress
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import models.users.wanderer_profile.profile.UserLoginDetails
import repositories.user_profile.{UserLoginDetailsRepositoryAlgebra, WandererAddressRepositoryAlgebra, WandererPersonalDetailsRepositoryAlgebra}
import services.authentication.password.PasswordServiceAlgebra

import java.time.LocalDateTime

object RegistrationServiceMocks {

  class MockUserLoginDetailsRepository(
                                        existingUserLoginDetails: Map[String, UserLoginDetails] = Map.empty
                                      ) extends UserLoginDetailsRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, UserLoginDetails]] = IO.pure(existingUserLoginDetails)

    override def createUserLoginDetails(user: UserLoginDetails): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def findByUserId(userId: String): IO[Option[UserLoginDetails]] = IO.pure(existingUserLoginDetails.get(userId))

    override def findByUsername(username: String): IO[Option[UserLoginDetails]] = IO.pure(existingUserLoginDetails.get(username))

    override def findByEmail(email: String): IO[Option[UserLoginDetails]] = IO.pure(existingUserLoginDetails.values.find(_.email.contains(email)))

    override def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): IO[Option[UserLoginDetails]] = ???

    override def updateUserLoginDetailsDynamic(userId: String, username: Option[String], passwordHash: Option[String], email: Option[String], role: Option[Role]): IO[Option[UserLoginDetails]] = ???
  }

  class MockWandererAddressRepository(
                                       existingWandererAddress: Map[String, WandererAddress] = Map.empty
                                     ) extends WandererAddressRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, WandererAddress]] = IO.pure(existingWandererAddress)

    override def findByUserId(userId: String): IO[Option[WandererAddress]] = IO.pure(existingWandererAddress.get(userId))

    override def createUserAddress(user: WandererAddress): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def updateAddressDynamic(userId: String, street: Option[String], city: Option[String], country: Option[String], county: Option[String], postcode: Option[String]): IO[Option[WandererAddress]] = ???

    override def createRegistrationWandererAddress(userId: String): IO[Int] =
      IO.pure(1)
  }


  class MockWandererPersonalDetailsRepository(
                                                    existingWandererAddress: Map[String, WandererPersonalDetails] = Map.empty
                                                  )
    extends WandererPersonalDetailsRepositoryAlgebra[IO] {

    override def findByUserId(userId: String): IO[Option[WandererPersonalDetails]] =
      IO.pure(existingWandererAddress.get(userId))

    override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): IO[Int] =
      IO.pure(1)

    override def updatePersonalDetailsDynamic(
                                               userId: String,
                                               contactNumber: Option[String],
                                               firstName: Option[String],
                                               lastName: Option[String],
                                               email: Option[String],
                                               company: Option[String]
                                             ): IO[Option[WandererPersonalDetails]] = ???

    override def createRegistrationPersonalDetails(userId: String): IO[Int] =
      IO.pure(1)
    
  }

  class MockPasswordService(
                             passwordValidationResult: Validated[List[RegisterPasswordErrors], String],
                             hashedPassword: String
                           ) extends PasswordServiceAlgebra[IO] {
    override def validatePassword(plainTextPassword: String): Validated[List[RegisterPasswordErrors], String] = passwordValidationResult

    override def hashPassword(plainTextPassword: String): IO[String] = IO.pure(hashedPassword)

    override def checkPassword(plainTextPassword: String, hashedPassword: String): IO[Boolean] = IO.pure(true)
  }
}
