package configuration

import cats.effect.IO
import configuration.models.{AppConfig, IntegrationSpecConfig, PostgresqlConfig, ServerConfig}
import weaver.SimpleIOSuite

object AppConfigSpec extends SimpleIOSuite {

  val configReader: ConfigReaderAlgebra[IO] = ConfigReader[IO]

  test("load test configurations correctly") {

    val serverConfig = ServerConfig("0.0.0.0", 8081)

    val postgresqlConfig =
      PostgresqlConfig(
        dbName = "shared_test_db",
        host = "localhost",
        port = 5432,
        username = "shared_user",
        password = "share"
      )

    val integrationSpecConfig =
      IntegrationSpecConfig(
        host = "127.0.0.1",
        port = 9999,
        postgresqlConfig
      )

    val appConfig = AppConfig(serverConfig, integrationSpecConfig)

    for {
      config <- configReader.loadAppConfig
    } yield {
      expect.all(
        config.serverConfig == serverConfig,
        config == appConfig,
        config.integrationSpecConfig.postgresqlConfig == postgresqlConfig,
        config.integrationSpecConfig == integrationSpecConfig
      )
    }
  }
}
