package middleware

import cats.effect.Temporal
import org.http4s.HttpRoutes
import org.http4s.server.middleware.Throttle

import scala.concurrent.duration.DurationInt

object Middleware {

  // Throttle middleware to apply rate limiting
  def throttleMiddleware[F[_] : Temporal](routes: HttpRoutes[F]): F[HttpRoutes[F]] = {
    Throttle.httpRoutes(
      amount = 500, // Maximum number of requests
      per = 1.minute // Time period for requests allowed, refreshes tokens in the bucket to allow for 500 requests per minute
    )(routes) // Apply throttling to the routes, returns F[HttpRoutes[F]]
  }

}
