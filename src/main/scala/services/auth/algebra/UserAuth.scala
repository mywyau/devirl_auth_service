package services.auth.algebra

import models.users.wanderer_profile.profile.UserProfile

case class UserAuth[F[_]](user: UserProfile)
