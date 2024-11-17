package controllers.users.wanderer_profile.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.effect.IO
import models.users.*
import models.users.wanderer_profile.errors.{UserIdNotFound, WandererProfileErrors}
import models.users.wanderer_profile.profile.WandererUserProfile
import models.users.wanderer_profile.requests.{UpdateAddress, UpdateLoginDetails, UpdatePersonalDetails}
import services.wanderer_profile.WandererProfileServiceAlgebra

class MockWandererProfileService(userProfileData: Map[String, WandererUserProfile])
  extends WandererProfileServiceAlgebra[IO] {

  override def createProfile(user_id: String): IO[ValidatedNel[WandererProfileErrors, WandererUserProfile]] = {
    userProfileData.get(user_id) match {
      case Some(profile) => IO.pure(Valid(profile))
      case None => IO.pure(Invalid(NonEmptyList.of(UserIdNotFound)))
    }
  }

  override def updateProfile(
                              userId: String,
                              loginDetailsUpdate: Option[UpdateLoginDetails],
                              addressUpdate: Option[UpdateAddress],
                              personalDetailsUpdate: Option[UpdatePersonalDetails]
                            ): IO[Option[WandererUserProfile]] = {
    val updatedProfile = userProfileData.get(userId).map { profile =>
      profile.copy(
        userLoginDetails = loginDetailsUpdate.flatMap { ld =>
          profile.userLoginDetails.map(_.copy(
            username = ld.username.getOrElse(profile.userLoginDetails.get.username),
            password_hash = ld.passwordHash.getOrElse(profile.userLoginDetails.get.password_hash),
            email = ld.email.getOrElse(profile.userLoginDetails.get.email)
          ))
        }.orElse(profile.userLoginDetails),
        userAddress = addressUpdate.flatMap { addr =>
          profile.userAddress.map(_.copy(
            street = addr.street.orElse(profile.userAddress.flatMap(_.street)),
            city = addr.city.orElse(profile.userAddress.flatMap(_.city)),
            country = addr.country.orElse(profile.userAddress.flatMap(_.country)),
            county = addr.county.orElse(profile.userAddress.flatMap(_.county)),
            postcode = addr.postcode.orElse(profile.userAddress.flatMap(_.postcode))
          ))
        }.orElse(profile.userAddress),
        userPersonalDetails = personalDetailsUpdate.flatMap { pd =>
          profile.userPersonalDetails.map(_.copy(
            first_name = pd.firstName.orElse(profile.userPersonalDetails.flatMap(_.first_name)),
            last_name = pd.lastName.orElse(profile.userPersonalDetails.flatMap(_.last_name)),
            contact_number = pd.contactNumber.orElse(profile.userPersonalDetails.flatMap(_.contact_number)),
            email = pd.email.orElse(profile.userPersonalDetails.flatMap(_.email)),
            company = pd.company.orElse(profile.userPersonalDetails.flatMap(_.company))
          ))
        }.orElse(profile.userPersonalDetails)
      )
    }
    IO.pure(updatedProfile)
  }

}
