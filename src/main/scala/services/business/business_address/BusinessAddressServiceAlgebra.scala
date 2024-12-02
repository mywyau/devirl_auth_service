package services.business.business_address

import cats.data.ValidatedNel
import models.business.business_address.errors.BusinessAddressErrors
import models.business.business_address.service.BusinessAddress
import models.database.SqlErrors


trait BusinessAddressServiceAlgebra[F[_]] {

  def getAddressDetailsByUserId(userId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(wandererAddress: BusinessAddress): F[ValidatedNel[SqlErrors, Int]]
}