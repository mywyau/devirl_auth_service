package services.office.office_address

import models.office.office_address.OfficeAddress
import models.office.office_address.errors.OfficeAddressErrors

trait OfficeAddressServiceAlgebra[F[_]] {

  def getAddressByBusinessId(userId: String): F[Either[OfficeAddressErrors, OfficeAddress]]

  def createOfficeAddress(wandererAddress: OfficeAddress): F[Int]
}