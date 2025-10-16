package configuration.constants

import configuration.models.*
import configuration.AppConfig

object ProdAppConfigConstants {

  val featureSwitches =
    FeatureSwitches(
      useDockerHost = false,
      localTesting = false,
      useCors = false,
    )

  val devIrlFrontendConfig =
    DevIrlFrontendConfig(
      host = "0.0.0.0",
      port = 8080,
      baseUrl = "https://devirl.com"
    )

  val appServerConfig =
    ServerConfig(
      host = "0.0.0.0",
      port = 8080
    )

  val containerPostgreSqlConfig =
    PostgresqlConfig(
      dbName = "dev_quest_db",
      dockerHost = "dev-quest-container",
      host = "localhost",
      port = 5432,
      username = "dev_quest_user",
      password = "turnip",
      maxPoolSize = 42
    )

  val redisConfig =
    RedisConfig(
      dockerHost = "redis-container",
      host = "localhost",
      port = 6379
    )


  val prodAppConfigConstant =
    AppConfig(
      featureSwitches = featureSwitches,
      devIrlFrontendConfig = devIrlFrontendConfig,
      redisConfig = redisConfig,
      postgresqlConfig = containerPostgreSqlConfig,
      serverConfig = appServerConfig,
    )
}
