package services.wanderer_profile

import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import models.users.wanderer_address.service.WandererAddress
import models.users.wanderer_profile.errors.{MissingAddress, MissingPersonalDetails, MissingLoginDetails, WandererProfileErrors}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, WandererUserProfile}
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
          street = details.street,
          city = details.city,
          country = details.country,
          county = details.county,
          postcode = details.postcode,
          created_at = details.created_at,
          updated_at = details.updated_at
        )
      )
      .toValidNel(MissingAddress)

  private def validatePersonalDetails(personalDetails: Option[WandererPersonalDetails]): ValidatedNel[WandererProfileErrors, WandererPersonalDetails] =
    personalDetails
      .toValidNel(MissingPersonalDetails)

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
          first_name = Some(personalDetails.first_name), 
          last_name = Some(personalDetails.last_name),
          userAddress = Some(userAddress),
          contact_number = Some(personalDetails.contact_number),
          email = Some(loginDetails.email),
          company = Some(personalDetails.company),
          role = Some(loginDetails.role),
          created_at = loginDetails.created_at,
          updated_at = loginDetails.updated_at
        )
      }
    }
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
