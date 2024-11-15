package services.wanderer_contact_details

import models.users.*
import models.users.contact_details.errors.ContactDetailsErrors
import models.users.contact_details.service.WandererContactDetails

trait WandererContactDetailsServiceAlgebra[F[_]] {

  def getContactDetailsByUserId(userId: String): F[Either[ContactDetailsErrors, WandererContactDetails]]

  def createContactDetails(wandererAddress: WandererContactDetails): F[Int]
}