package services.auth.algebra

import models.users.UserProfile

case class UserAuth[F[_]](user: UserProfile)
