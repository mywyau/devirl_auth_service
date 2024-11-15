package controllers.users.wanderer_profile.mocks

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.effect.IO
import models.users.*
import models.users.wanderer_profile.errors.{UserIdNotFound, WandererProfileErrors}
import models.users.wanderer_profile.profile.WandererUserProfile
import services.wanderer_profile.WandererProfileServiceAlgebra

class MockWandererProfileService(userProfileData: Map[String, WandererUserProfile])
  extends WandererProfileServiceAlgebra[IO] {

  override def createProfile(user_id: String): IO[ValidatedNel[WandererProfileErrors, WandererUserProfile]] = {
    userProfileData.get(user_id) match {
      case Some(profile) => IO.pure(Valid(profile))
      case None => IO.pure(Invalid(NonEmptyList.of(UserIdNotFound)))
    }
  }
}
