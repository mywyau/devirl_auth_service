package controllers.registration

import cats.data.Validated
import cats.effect.IO
import models.users.registration.RegistrationErrors
import models.wanderer.wanderer_profile.profile.UserLoginDetails
import models.wanderer.wanderer_profile.requests.UserSignUpRequest
import services.authentication.registration.RegistrationServiceAlgebra

class MockRegistrationService(
                               registerUserMock: UserSignUpRequest => IO[Validated[List[RegistrationErrors], UserLoginDetails]]
                             ) extends RegistrationServiceAlgebra[IO] {


  override def registerUser(request: UserSignUpRequest): IO[Validated[List[RegistrationErrors], UserLoginDetails]] =
    registerUserMock(request)
}
