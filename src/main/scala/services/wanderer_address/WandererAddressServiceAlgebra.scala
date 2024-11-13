package services.wanderer_address

import models.users.*
import models.users.wanderer_address.errors.WandererAddressErrors
import models.users.wanderer_address.service.WandererAddress

trait WandererAddressServiceAlgebra[F[_]] {

  def getAddressDetailsByUserId(userId: String): F[Either[WandererAddressErrors, WandererAddress]]

  def createAddress(wandererAddress: WandererAddress): F[Int]
}