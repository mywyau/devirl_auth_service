package shared

import infrastructure.SessionCacheAlgebra
import cats.effect.IO

final case class SessionCacheResource(sessionCache: SessionCacheAlgebra[IO])
