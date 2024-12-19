package services.business.address

import cats.data.ValidatedNel
import models.business.business_address.errors.BusinessAddressErrors
import models.business.business_address.requests.BusinessAddressRequest
import models.business.business_address.service.BusinessAddress
import models.database.SqlErrors


trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(businessAddressRequest: BusinessAddressRequest): F[ValidatedNel[SqlErrors, Int]]
}