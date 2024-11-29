package services.office.office_contact_details

import models.office.office_contact_details.OfficeContactDetails
import models.office.office_contact_details.errors.OfficeContactDetailsErrors

trait OfficeContactDetailsServiceAlgebra[F[_]] {

  def getContactDetailsByBusinessId(businessId: String): F[Either[OfficeContactDetailsErrors, OfficeContactDetails]]

  def createOfficeContactDetails(officeContactDetails: OfficeContactDetails): F[cats.data.ValidatedNel[OfficeContactDetailsErrors, Int]]
}