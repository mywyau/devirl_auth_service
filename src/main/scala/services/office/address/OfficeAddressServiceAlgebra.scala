package services.office.address

import cats.data.ValidatedNel
import models.database.SqlErrors
import models.office.address_details.OfficeAddress
import models.office.address_details.errors.OfficeAddressErrors

trait OfficeAddressServiceAlgebra[F[_]] {

  def getAddressByBusinessId(userId: String): F[Either[OfficeAddressErrors, OfficeAddress]]

  def createOfficeAddress(officeAddress: OfficeAddress): F[ValidatedNel[OfficeAddressErrors, Int]]
}