package configuration

import cats.kernel.Eq
import configuration.models.*
import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class AppConfig(
  featureSwitches: FeatureSwitches,
  devIrlFrontendConfig: DevIrlFrontendConfig,
  redisConfig: RedisConfig,
  kafkaConfig: KafkaConfig,
  postgresqlConfig: PostgresqlConfig,
  serverConfig: ServerConfig
) derives ConfigReader
