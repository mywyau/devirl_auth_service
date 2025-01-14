package mocks

import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.IO
import cats.implicits.*
import models.database.*
import models.office_listing.OfficeListing
import models.office_listing.OfficeListingCard
import models.office_listing.requests.InitiateOfficeListingRequest
import models.office_listing.requests.OfficeListingRequest
import repositories.office.OfficeListingRepositoryAlgebra

class MockOfficeListingRepository(
  findByOfficeIdResult: IO[Option[OfficeListing]],
  listingResult: IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
) extends OfficeListingRepositoryAlgebra[IO] {

  override def findAll(businessId: String): IO[List[OfficeListing]] = ???

  override def findByOfficeId(officeId: String): IO[Option[OfficeListing]] = findByOfficeIdResult

  override def initiate(request: InitiateOfficeListingRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = listingResult

  override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def deleteByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

}
