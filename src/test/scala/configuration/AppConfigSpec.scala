package configuration

import cats.effect.IO
import configuration.models.{AppConfig, ServerConfig, IspecServerConfig}
import weaver.SimpleIOSuite

object AppConfigSpec extends SimpleIOSuite {

  val configReader: ConfigReaderAlgebra[IO] = ConfigReader[IO]

  test("load test configurations correctly") {

    val serverConfig = ServerConfig("0.0.0.0", 8080)
    val ispecServerConfig = IspecServerConfig("127.0.0.1", 9999)
    val appConfig = AppConfig(serverConfig, ispecServerConfig)

    for {
      config <- configReader.loadAppConfig
    } yield {
      expect.all(
        config.serverConfig == serverConfig,
        config.ispecServerConfig == ispecServerConfig,
        config == appConfig
      )
    }
  }
}
