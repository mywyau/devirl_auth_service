package services.wanderer_personal_details

import models.users.*
import models.users.wanderer_personal_details.errors.PersonalDetailsErrors
import models.users.wanderer_personal_details.service.WandererPersonalDetails

trait WandererPersonalDetailsServiceAlgebra[F[_]] {

  def getPersonalDetailsByUserId(userId: String): F[Either[PersonalDetailsErrors, WandererPersonalDetails]]

  def createPersonalDetails(wandererAddress: WandererPersonalDetails): F[Int]
}