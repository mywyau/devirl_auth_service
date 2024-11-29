package controllers.constants

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.desk_listing.DeskListingController
import controllers.fragments.DeskListingControllerFragments.{createDeskListingTable, resetDeskListingTable}
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.business.adts.PrivateDesk
import models.business.desk_listing.Availability
import models.business.desk_listing.requests.DeskListingRequest
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.DeskListingRepositoryImpl
import services.business.desk_listing.DeskListingServiceImpl
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

object DeskListingConstants {

  val availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
      endTime = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
    )

  val testDeskListingRequest =
    DeskListingRequest(
      business_id = "business_123",
      workspace_id = "workspace_456",
      title = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      desk_type = PrivateDesk,
      quantity = 5,
      price_per_hour = BigDecimal(15.50),
      price_per_day = BigDecimal(120.00),
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability,
      created_at = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
      updated_at = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
    )
}
