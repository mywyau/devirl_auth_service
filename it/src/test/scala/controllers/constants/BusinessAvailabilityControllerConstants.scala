package controllers.constants

import models.*
import models.business.availability.RetrieveSingleBusinessAvailability
import testData.BusinessTestConstants.*
import testData.TestConstants.*

import java.time.LocalTime

object BusinessAvailabilityControllerConstants {

  val mondayAvailability: RetrieveSingleBusinessAvailability =
    RetrieveSingleBusinessAvailability(
      Monday,
      Some(LocalTime.of(9, 0, 0)),
      Some(LocalTime.of(17, 0, 0))
    )

  val tuesdayAvailability: RetrieveSingleBusinessAvailability =
    RetrieveSingleBusinessAvailability(
      Tuesday,
      Some(LocalTime.of(9, 0, 0)),
      Some(LocalTime.of(17, 0, 0))
    )

  val wednesdayAvailability: RetrieveSingleBusinessAvailability =
    RetrieveSingleBusinessAvailability(
      Wednesday,
      Some(LocalTime.of(9, 0, 0)),
      Some(LocalTime.of(17, 0, 0))
    )
}
