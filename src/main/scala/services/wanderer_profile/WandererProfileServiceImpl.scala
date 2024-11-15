package services.wanderer_profile

import cats.data.*
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.wanderer_personal_details.service.WandererContactDetails
import models.users.wanderer_address.service.WandererAddress
import models.users.wanderer_profile.errors.{MissingAddress, MissingContactDetails, MissingLoginDetails, WandererProfileErrors}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, WandererUserProfile}
import repositories.users.{UserLoginDetailsRepositoryAlgebra, WandererAddressRepositoryAlgebra, WandererContactDetailsRepositoryAlgebra}
import services.password.PasswordServiceAlgebra

class WandererProfileServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                userLoginDetailsRepo: UserLoginDetailsRepositoryAlgebra[F],
                                                                                wandererAddressRepo: WandererAddressRepositoryAlgebra[F],
                                                                                wandererContactDetailsRepo: WandererContactDetailsRepositoryAlgebra[F],
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

  private def validateContactDetails(contactDetails: Option[WandererContactDetails]): ValidatedNel[WandererProfileErrors, String] =
    contactDetails
      .map(_.contact_number)
      .toValidNel(MissingContactDetails)

  override def createProfile(user_id: String): F[ValidatedNel[WandererProfileErrors, WandererUserProfile]] = {
    for {
      loginDetailsOpt <- userLoginDetailsRepo.findByUserId(user_id)
      addressOpt <- wandererAddressRepo.findByUserId(user_id)
      contactDetailsOpt <- wandererContactDetailsRepo.findByUserId(user_id)
    } yield {
      (
        validateLoginDetails(loginDetailsOpt),
        validateAddress(addressOpt),
        validateContactDetails(contactDetailsOpt)
      ).mapN { (loginDetails, userAddress, contactNumber) =>
        WandererUserProfile(
          userId = user_id,
          userLoginDetails = Some(loginDetails),
          first_name = Some(""), // or apply further validations if needed
          last_name = Some(""), // or apply further validations if needed
          userAddress = Some(userAddress),
          contact_number = Some(contactNumber),
          email = Some(loginDetails.email),
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
                                                           wandererContactDetailsRepo: WandererContactDetailsRepositoryAlgebra[F],
                                                           passwordService: PasswordServiceAlgebra[F]
                                                         ): WandererProfileServiceAlgebra[F] =
    new WandererProfileServiceImpl[F](userLoginDetailsRepo, wandererAddressRepo, wandererContactDetailsRepo, passwordService)
}
