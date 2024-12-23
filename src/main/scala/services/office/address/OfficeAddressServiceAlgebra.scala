package services.office.address

import cats.data.ValidatedNel
import models.database.SqlErrors
import models.office.address_details.OfficeAddress
import models.office.address_details.requests.OfficeAddressRequest
import models.office.address_details.errors.OfficeAddressErrors

trait OfficeAddressServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Either[OfficeAddressErrors, OfficeAddress]]

  def create(officeAddress: OfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]
}