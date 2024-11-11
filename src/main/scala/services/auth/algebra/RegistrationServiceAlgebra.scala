package services.auth.algebra

import cats.data.*
import models.users.*
import models.auth.*
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest

trait RegistrationServiceAlgebra[F[_]] {

  def registerUser(request: UserSignUpRequest): F[Validated[List[RegistrationErrors], UserLoginDetails]]
}