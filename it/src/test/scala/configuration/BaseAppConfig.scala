package configuration

import cats.effect.*
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import configuration.models.*
import configuration.AppConfig

trait BaseAppConfig {

  val configReader: ConfigReaderAlgebra[IO] = ConfigReader[IO]

  def appConfigResource: Resource[IO, AppConfig] =
    Resource.eval(
      configReader.loadAppConfig
        .handleErrorWith { e =>
          IO.raiseError(new RuntimeException(s"[ControllerSharedResource] Failed to load app configuration: ${e.getMessage}", e))
        }
    )
}
