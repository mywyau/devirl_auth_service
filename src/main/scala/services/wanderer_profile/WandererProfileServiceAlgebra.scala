package services.wanderer_profile

import cats.data.*
import models.users.*
import models.users.wanderer_profile.errors.WandererProfileErrors
import models.users.wanderer_profile.profile.WandererUserProfile

trait WandererProfileServiceAlgebra[F[_]] {

  def createProfile(user_id: String): F[ValidatedNel[WandererProfileErrors, WandererUserProfile]]
}