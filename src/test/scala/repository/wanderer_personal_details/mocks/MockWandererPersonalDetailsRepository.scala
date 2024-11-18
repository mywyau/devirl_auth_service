package repository.wanderer_personal_details.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repositories.users.WandererPersonalDetailsRepositoryAlgebra

import java.time.LocalDateTime

case class MockWandererPersonalDetailsRepository(ref: Ref[IO, List[WandererPersonalDetails]])
  extends WandererPersonalDetailsRepositoryAlgebra[IO] {

  override def findByUserId(userId: String): IO[Option[WandererPersonalDetails]] =
    ref.get.map(_.find(_.userId == userId))

  override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): IO[Int] =
    ref.modify(details => (wandererPersonalDetails :: details, 1))

  override def updatePersonalDetailsDynamic(
                                             userId: String,
                                             contactNumber: Option[String],
                                             firstName: Option[String],
                                             lastName: Option[String],
                                             email: Option[String],
                                             company: Option[String]
                                           ): IO[Option[WandererPersonalDetails]] =
    ref.modify { details =>
      details.find(_.userId == userId) match {
        case Some(existing) =>
          val updated =
            existing.copy(
              contactNumber = contactNumber,
              firstName = firstName,
              lastName = lastName,
              email = email,
              company = company,
              updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
            )
          (details.map(d => if (d.userId == userId) updated else d), Some(updated))
        case None => (details, None)
      }
    }

  override def createRegistrationPersonalDetails(userId: String): IO[Int] =
    IO.pure(1)
}
