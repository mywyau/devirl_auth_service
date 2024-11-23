package services.business.business_address

import models.business.business_address.errors.BusinessAddressErrors
import models.business.business_address.service.BusinessAddress


trait BusinessAddressServiceAlgebra[F[_]] {

  def getAddressDetailsByUserId(userId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(wandererAddress: BusinessAddress): F[Int]
}