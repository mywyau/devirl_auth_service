package services.authentication.registration

import cats.data.*
import models.authentication.*
import models.users.*
import models.users.registration.RegistrationErrors
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest

trait RegistrationServiceAlgebra[F[_]] {

  def registerUser(request: UserSignUpRequest): F[Validated[List[RegistrationErrors], UserLoginDetails]]
}