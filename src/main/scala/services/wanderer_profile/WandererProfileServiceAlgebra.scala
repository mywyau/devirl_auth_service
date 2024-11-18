package services.wanderer_profile

import cats.data.*
import models.users.*
import models.users.wanderer_profile.errors.WandererProfileErrors
import models.users.wanderer_profile.profile.WandererUserProfile
import models.users.wanderer_profile.requests.*

trait WandererProfileServiceAlgebra[F[_]] {

  def createProfile(userId: String): F[ValidatedNel[WandererProfileErrors, WandererUserProfile]]

  def updateProfile(
                     userId: String,
                     loginDetailsUpdate: Option[UpdateLoginDetails],
                     addressUpdate: Option[UpdateAddress],
                     personalDetailsUpdate: Option[UpdatePersonalDetails]
                   ): F[Option[WandererUserProfile]]
}