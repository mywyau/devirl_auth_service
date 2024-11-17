package services.wanderer_profile

import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.wanderer_address.service.WandererAddress
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import models.users.wanderer_profile.errors.{MissingAddress, MissingLoginDetails, MissingPersonalDetails, WandererProfileErrors}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}
import models.users.wanderer_profile.requests.*
import repositories.users.{UserLoginDetailsRepositoryAlgebra, WandererAddressRepositoryAlgebra, WandererPersonalDetailsRepositoryAlgebra}
import services.password.PasswordServiceAlgebra

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
          userId = details.user_id,
          street = Some(details.street),
          city = Some(details.city),
          country = Some(details.country),
          county = details.county,
          postcode = Some(details.postcode),
          created_at = details.created_at,
          updated_at = details.updated_at
        )
      )
      .toValidNel(MissingAddress)

  private def validatePersonalDetails(personalDetails: Option[WandererPersonalDetails]): ValidatedNel[WandererProfileErrors, UserPersonalDetails] =
    personalDetails
      .map(
        details =>
          UserPersonalDetails(
            user_id = details.user_id,
            first_name = Some(details.first_name),
            last_name = Some(details.last_name),
            contact_number = Some(details.contact_number),
            email = Some(details.email),
            company = Some(details.company),
            created_at = details.created_at,
            updated_at = details.updated_at
          )
      ).toValidNel(MissingPersonalDetails)

  override def createProfile(user_id: String): F[ValidatedNel[WandererProfileErrors, WandererUserProfile]] = {
    for {
      loginDetailsOpt <- userLoginDetailsRepo.findByUserId(user_id)
      addressOpt <- wandererAddressRepo.findByUserId(user_id)
      personalDetailsOpt <- wandererPersonalDetailsRepo.findByUserId(user_id)
    } yield {
      (
        validateLoginDetails(loginDetailsOpt),
        validateAddress(addressOpt),
        validatePersonalDetails(personalDetailsOpt)
      ).mapN { (loginDetails, userAddress, personalDetails) =>
        WandererUserProfile(
          userId = user_id,
          userLoginDetails = Some(loginDetails),
          userPersonalDetails = Some(personalDetails),
          userAddress = Some(userAddress),
          role = Some(loginDetails.role),
          created_at = loginDetails.created_at,
          updated_at = loginDetails.updated_at
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
        case Some(UpdatePersonalDetails(contactNumber, firstName, lastName, email, company)) =>
          wandererPersonalDetailsRepo.updatePersonalDetailsDynamic(userId, contactNumber, firstName, lastName, email, company)
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
