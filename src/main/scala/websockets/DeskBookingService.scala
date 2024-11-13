package websockets

import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import fs2.Stream
import fs2.concurrent.Topic
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame

trait DeskBookingService[F[_]] {
  def deskUpdatesTopic: Resource[F, Topic[F, String]]
  def deskAvailabilityWebSocket(builder: WebSocketBuilder2[F]): HttpRoutes[F]
}

class DeskBookingServiceImpl[F[_]: Concurrent] extends DeskBookingService[F] with Http4sDsl[F] {

  override def deskUpdatesTopic: Resource[F, Topic[F, String]] =
    Topic[F, String].toResource.evalMap { topic =>
      topic.publish1("Welcome to Desk Booking").as(topic)
    }

  override def deskAvailabilityWebSocket(builder: WebSocketBuilder2[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "ws" / "desk-availability" =>
        deskUpdatesTopic.use { topic =>
          builder.build(
            send = topic.subscribe(100).map(WebSocketFrame.Text(_)),
            receive = (stream: Stream[F, WebSocketFrame]) =>
              stream.evalMap {
                case WebSocketFrame.Text(msg, _) => topic.publish1(s"Desk update: $msg").void
                case _ => Concurrent[F].unit
              }
          )
        }
    }
  }
}
