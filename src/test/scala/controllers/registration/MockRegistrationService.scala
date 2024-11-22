package controllers.registration

import cats.data.Validated
import cats.effect.IO
import models.users.registration.RegistrationErrors
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest
import services.authentication.registration.RegistrationServiceAlgebra

class MockRegistrationService(
                               registerUserMock: UserSignUpRequest => IO[Validated[List[RegistrationErrors], UserLoginDetails]]
                             ) extends RegistrationServiceAlgebra[IO] {


  override def registerUser(request: UserSignUpRequest): IO[Validated[List[RegistrationErrors], UserLoginDetails]] =
    registerUserMock(request)
}
