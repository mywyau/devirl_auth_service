package services.auth.algebra

import cats.data.*
import models.users.*

trait RegistrationServiceAlgebra[F[_]] {

//  def signUp(request: SignUpRequest): F[Validated[List[String], UserProfile]]
  
  def registerUser(request: UserRegistrationRequest): F[Validated[List[String], UserProfile]]
}