package services.wanderer_profile

import cats.data.*
import models.auth.*
import models.users.*
import models.users.wanderer_profile.errors.WandererProfileErrors
import models.users.wanderer_profile.profile.{UserLoginDetails, WandererUserProfile}
import models.users.wanderer_profile.requests.UserSignUpRequest

trait WandererProfileServiceAlgebra[F[_]] {

  def createProfile(user_id: String): F[ValidatedNel[WandererProfileErrors, WandererUserProfile]]
}