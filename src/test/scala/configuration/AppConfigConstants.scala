package configuration

import configuration.models.{AppConfig, IntegrationSpecConfig, PostgresqlConfig, ServerConfig}

object AppConfigConstants {

  val serverConfig =
    ServerConfig("0.0.0.0", 8081)

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


}
