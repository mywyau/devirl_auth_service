package services.business_desk

import models.business.business_desk.errors.BusinessDeskErrors
import models.business.business_desk.requests.BusinessDeskRequest
import models.business.business_desk.service.BusinessDesk
import models.users.*


trait BusinessDeskServiceAlgebra[F[_]] {

  def findByUserId(userId: String): F[Either[BusinessDeskErrors, BusinessDesk]]

  def createDesk(businessDesk: BusinessDeskRequest): F[Either[BusinessDeskErrors, Int]]
}