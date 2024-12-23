package services.business.address

import cats.data.ValidatedNel
import models.business.address.BusinessAddress
import models.business.address.errors.BusinessAddressErrors
import models.business.address.requests.BusinessAddressRequest
import models.database.SqlErrors


trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(businessAddressRequest: BusinessAddressRequest): F[ValidatedNel[SqlErrors, Int]]

  def deleteAddress(businessId: String): F[ValidatedNel[SqlErrors, Int]]
}