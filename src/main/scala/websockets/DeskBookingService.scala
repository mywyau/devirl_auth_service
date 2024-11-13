package websockets

import cats.effect.*
import cats.implicits.*
import fs2.Stream
import fs2.concurrent.Topic
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame

// Desk case class definition
case class Desk(id: String, isAvailable: Boolean)

trait DeskBookingService[F[_]] {

  def deskAvailabilityWebSocket(builder: WebSocketBuilder2[F]): HttpRoutes[F]
}

class DeskBookingServiceImpl[F[_] : Concurrent](
                                                 topic: Topic[F, Desk] // Pass the shared topic here
                                               ) extends DeskBookingService[F] with Http4sDsl[F] {

  // Initialize desks for the example
  private var desks: Map[String, Desk] =
    Map(
      "desk1" -> Desk("desk1", isAvailable = true),
      "desk2" -> Desk("desk2", isAvailable = true),
      "desk3" -> Desk("desk3", isAvailable = true),
      "desk4" -> Desk("desk4", isAvailable = true),
      "desk5" -> Desk("desk5", isAvailable = true),
      "desk6" -> Desk("desk6", isAvailable = true),
      "desk7" -> Desk("desk7", isAvailable = true)
    )

  override def deskAvailabilityWebSocket(builder: WebSocketBuilder2[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "ws" / "desk-availability" =>
        builder.build(
          send = {
            // Send the initial state as Text frames, without publishing to the topic
            val initialStateStream = Stream.emits(desks.values.toSeq)
              .map(desk => WebSocketFrame.Text(desk.asJson.noSpaces))

            // Real-time updates stream via topic subscription
            val updatesStream = topic.subscribe(100).map(desk => WebSocketFrame.Text(desk.asJson.noSpaces))

            // Combine the initial state and updates
            initialStateStream ++ updatesStream
          },

          receive = (stream: Stream[F, WebSocketFrame]) =>
            stream.evalMap {
              case WebSocketFrame.Text(msg, _) => handleClientMessage(msg).void
              case _ => Concurrent[F].unit
            }
        )
    }
  }


  // Add a handler for the "ping" message in your WebSocket server
  private def handleClientMessage(msg: String): F[Unit] = {
    io.circe.parser.decode[ClientCommand](msg) match {
      case Right(ClientCommand("ping", _)) =>
        Concurrent[F].unit // Responding with no action
      case Right(ClientCommand("book", deskId)) =>
        updateDeskAvailability(deskId, isAvailable = false)
      case Right(ClientCommand("release", deskId)) =>
        updateDeskAvailability(deskId, isAvailable = true)
      case _ => Concurrent[F].unit // Ignore unknown commands
    }
  }


    // Update desk availability and publish the change to the topic
  private def updateDeskAvailability(deskId: String, isAvailable: Boolean): F[Unit] = {
    desks.get(deskId) match {
      case Some(desk) =>
        val updatedDesk = desk.copy(isAvailable = isAvailable)
        desks = desks.updated(deskId, updatedDesk)
        topic.publish1(updatedDesk).void
      case None => Concurrent[F].unit // Desk not found; do nothing
    }
  }

  // Define client command structure
  case class ClientCommand(action: String, deskId: String)
}
