package configuration.constants

import configuration.AppConfig
import configuration.models.*

object LocalAppConfigConstants {

  val featureSwitches =
    FeatureSwitches(
      useDockerHost = false,
      localTesting = false,
      useCors = false
    )

  val devIrlFrontendConfig =
    DevIrlFrontendConfig(
      host = "0.0.0.0",
      port = 3000,
      baseUrl = "http://localhost:3000"
    )

  val appServerConfig =
    ServerConfig(
      host = "0.0.0.0",
      port = 8080
    )

  val containerPostgreSqlConfig =
    PostgresqlConfig(
      dbName = "dev_auth_db",
      dockerHost = "dev-auth-container",
      host = "localhost",
      port = 5432,
      username = "dev_auth_user",
      password = "turnip",
      maxPoolSize = 42
    )

  val redisConfig =
    RedisConfig(
      dockerHost = "redis-container",
      host = "localhost",
      port = 6379
    )

  val localAppConfigConstant =
    AppConfig(
      featureSwitches = featureSwitches,
      devIrlFrontendConfig = devIrlFrontendConfig,
      redisConfig = redisConfig,
      postgresqlConfig = containerPostgreSqlConfig,
      serverConfig = appServerConfig
    )
}
