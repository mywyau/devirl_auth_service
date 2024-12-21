package services.business.contact_details

import cats.data.ValidatedNel
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.errors.BusinessContactDetailsErrors
import models.database.SqlErrors


trait BusinessContactDetailsServiceAlgebra[F[_]] {

  def getContactDetailsByBusinessId(businessId: String): F[Either[BusinessContactDetailsErrors, BusinessContactDetails]]

  def createBusinessContactDetails(businessContactDetails: BusinessContactDetails): F[ValidatedNel[BusinessContactDetailsErrors, Int]]

  def deleteContactDetails(businessId: String): F[ValidatedNel[SqlErrors, Int]]

}