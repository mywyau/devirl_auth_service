package services.auth.algebra

import cats.data.*
import models.users.*
import models.auth.*
import models.users.database.UserLoginDetails
import models.users.requests.UserSignUpRequest

trait RegistrationServiceAlgebra[F[_]] {

  def registerUser(request: UserSignUpRequest): F[Validated[List[RegistrationErrors], UserLoginDetails]]
}