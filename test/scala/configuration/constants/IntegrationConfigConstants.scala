package configuration.constants

import configuration.models.*
import configuration.AppConfig

object IntegrationConfigConstants {

  val featureSwitches =
    FeatureSwitches(
      useDockerHost = false,
      localTesting = false,
      useCors = false,
    )

  val itDevIrlFrontendConfig =
    DevIrlFrontendConfig(
      host = "0.0.0.0",
      port = 3000,
      baseUrl = "http://localhost:3000"
    )

  val itSpecServerConfig =
    ServerConfig(
      host = "127.0.0.1",
      port = 9999
    )

  val itPostgresqlConfig =
    PostgresqlConfig(
      dbName = "dev_auth_test_db",
      dockerHost = "dev-auth-db-it",
      host = "localhost",
      port = 5434,
      username = "dev_auth_test_user",
      password = "turnip",
      maxPoolSize = 42
    )

  val itRedisConfig =
    RedisConfig(
      dockerHost = "redis-test-container",
      host = "localhost",
      port = 6380
    )

  val integrationAppConfigConstant =
    AppConfig(
      featureSwitches = featureSwitches,
      devIrlFrontendConfig = itDevIrlFrontendConfig,
      redisConfig = itRedisConfig,
      postgresqlConfig = itPostgresqlConfig,
      serverConfig = itSpecServerConfig,
    )
}
