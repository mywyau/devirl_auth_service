package services.business.address

import cats.data.ValidatedNel
import models.business.address_details.errors.BusinessAddressErrors
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import models.database.SqlErrors


trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(businessAddressRequest: BusinessAddressRequest): F[ValidatedNel[SqlErrors, Int]]
}