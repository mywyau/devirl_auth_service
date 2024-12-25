package configuration

import cats.effect.*
import com.comcast.ip4s.{Host, Port, ipv4, port}
import configuration.models.*
import configuration.{ConfigReader, ConfigReaderAlgebra}

trait BaseAppConfig {

  val configReader: ConfigReaderAlgebra[IO] = ConfigReader[IO]

  def configResource: Resource[IO, AppConfig] = {
    Resource.eval(
      configReader
        .loadAppConfig
        .handleErrorWith { e =>
          IO.raiseError(new RuntimeException(s"[ControllerSharedResource] Failed to load app configuration: ${e.getMessage}", e))
        }
    )
  }

  def hostResource(appConfig: AppConfig): Resource[IO, Host] = {
    Resource.eval(
      IO.fromEither(
        Host.fromString(appConfig.integrationSpecConfig.serverConfig.host)
          .toRight(new RuntimeException("[ControllerSharedResource] Invalid host configuration"))
      )
    )
  }

  def portResource(appConfig: AppConfig): Resource[IO, Port] = {
    Resource.eval(
      IO.fromEither(
        Port.fromInt(appConfig.integrationSpecConfig.serverConfig.port)
          .toRight(new RuntimeException("[ControllerSharedResource] Invalid port configuration"))
      )
    )
  }

  def postgresqlConfigResource(appConfig: AppConfig): Resource[IO, PostgresqlConfig] = {
    Resource.eval(
      IO(appConfig.integrationSpecConfig.postgresqlConfig)
    )
  }
}

