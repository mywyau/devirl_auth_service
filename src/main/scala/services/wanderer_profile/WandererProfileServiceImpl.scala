package services.wanderer_profile

import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.wanderer.wanderer_address.service.WandererAddress
import models.wanderer.wanderer_personal_details.service.WandererPersonalDetails
import models.wanderer.wanderer_profile.errors.{MissingAddress, MissingLoginDetails, MissingPersonalDetails, WandererProfileErrors}
import models.wanderer.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}
import models.wanderer.wanderer_profile.requests.*
import repositories.user_profile.{UserLoginDetailsRepositoryAlgebra, WandererAddressRepositoryAlgebra}
import repositories.wanderer.{WandererPersonalDetailsRepositoryAlgebra}
import services.authentication.password.PasswordServiceAlgebra


class WandererProfileServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                                                wandererAddressRepo: WandererAddressRepositoryAlgebra[F],
                                                                                wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryAlgebra[F],
                                                                                passwordService: PasswordServiceAlgebra[F]
                                                                              ) extends WandererProfileServiceAlgebra[F] {

  private def validateLoginDetails(loginDetails: Option[UserLoginDetails]): ValidatedNel[WandererProfileErrors, UserLoginDetails] =
    loginDetails.toValidNel(MissingLoginDetails)

  private def validateAddress(address: Option[WandererAddress]): ValidatedNel[WandererProfileErrors, UserAddress] =
    address
      .map(details =>
        UserAddress(
          userId = details.userId,
          street = details.street,
          city = details.city,
          country = details.country,
          county = details.county,
          postcode = details.postcode,
          createdAt = details.createdAt,
          updatedAt = details.updatedAt
        )
      )
      .toValidNel(MissingAddress)

  private def validatePersonalDetails(personalDetails: Option[WandererPersonalDetails]): ValidatedNel[WandererProfileErrors, UserPersonalDetails] =
    personalDetails
      .map(
        details =>
          UserPersonalDetails(
            userId = details.userId,
            firstName = details.firstName,
            lastName = details.lastName,
            contactNumber = details.contactNumber,
            email = details.email,
            company = details.company,
            createdAt = details.createdAt,
            updatedAt = details.updatedAt
          )
      ).toValidNel(MissingPersonalDetails)

  override def createProfile(userId: String): F[ValidatedNel[WandererProfileErrors, WandererUserProfile]] = {
    for {
      loginDetailsOpt <- userLoginDetailsRepo.findByUserId(userId)
      addressOpt <- wandererAddressRepo.findByUserId(userId)
      personalDetailsOpt <- wandererPersonalDetailsRepo.findByUserId(userId)
    } yield {
      (
        validateLoginDetails(loginDetailsOpt),
        validateAddress(addressOpt),
        validatePersonalDetails(personalDetailsOpt)
      ).mapN { (loginDetails, userAddress, personalDetails) =>
        WandererUserProfile(
          userId = userId,
          userLoginDetails = Some(loginDetails),
          userPersonalDetails = Some(personalDetails),
          userAddress = Some(userAddress),
          role = Some(loginDetails.role),
          createdAt = loginDetails.createdAt,
          updatedAt = loginDetails.updatedAt
        )
      }
    }
  }

  override def updateProfile(
                              userId: String,
                              loginDetailsUpdate: Option[UpdateLoginDetails],
                              addressUpdate: Option[UpdateAddress],
                              personalDetailsUpdate: Option[UpdatePersonalDetails]
                            ): F[Option[WandererUserProfile]] = {

    val updateLoginDetails =
      loginDetailsUpdate match {
        case Some(UpdateLoginDetails(username, passwordHash, email, role)) =>
          userLoginDetailsRepo.updateUserLoginDetailsDynamic(userId, username, passwordHash, email, role)
        case None => Monad[F].pure(None)
      }

    val updateAddress =
      addressUpdate match {
        case Some(UpdateAddress(street, city, country, county, postcode)) =>
          wandererAddressRepo.updateAddressDynamic(userId, street, city, country, county, postcode)
        case None => Monad[F].pure(None)
      }

    val updatePersonalDetails =
      personalDetailsUpdate match {
        case Some(UpdatePersonalDetails(firstName, lastName, contactNumber, email, company)) =>
          wandererPersonalDetailsRepo.updatePersonalDetailsDynamic(
            userId = userId, firstName = firstName, lastName = lastName, contactNumber = contactNumber, email = email, company = company
          )
        case None => Monad[F].pure(None)
      }

    for {
      _ <- updateLoginDetails
      _ <- updateAddress
      _ <- updatePersonalDetails
      updatedProfile <- createProfile(userId).map(_.toOption)
    } yield updatedProfile
  }
}


object WandererProfileService {
  def apply[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                           userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                           wandererAddressRepo: WandererAddressRepositoryAlgebra[F],
                                                           wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryAlgebra[F],
                                                           passwordService: PasswordServiceAlgebra[F]
                                                         ): WandererProfileServiceAlgebra[F] =
    new WandererProfileServiceImpl[F](userLoginDetailsRepo, wandererAddressRepo, wandererPersonalDetailsRepo, passwordService)
}
