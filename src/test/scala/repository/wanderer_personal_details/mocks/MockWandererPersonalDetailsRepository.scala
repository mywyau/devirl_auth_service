package repository.wanderer_personal_details.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repositories.users.WandererPersonalDetailsRepositoryAlgebra

import java.time.LocalDateTime

case class MockWandererPersonalDetailsRepository(ref: Ref[IO, List[WandererPersonalDetails]])
  extends WandererPersonalDetailsRepositoryAlgebra[IO] {

  override def findByUserId(userId: String): IO[Option[WandererPersonalDetails]] =
    ref.get.map(_.find(_.user_id == userId))

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
      details.find(_.user_id == userId) match {
        case Some(existing) =>
          val updated = existing.copy(
            contact_number = contactNumber.getOrElse(existing.contact_number),
            first_name = firstName.getOrElse(existing.first_name),
            last_name = lastName.getOrElse(existing.last_name),
            email = email.getOrElse(existing.email),
            company = company.getOrElse(existing.company),
            updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
          )
          (details.map(d => if (d.user_id == userId) updated else d), Some(updated))
        case None => (details, None)
      }
    }
}
